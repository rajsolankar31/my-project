package com.productintel.review.service;

import com.productintel.review.dto.*;
import com.productintel.review.entity.Review;
import com.productintel.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Review Intelligence Service.
 *
 * <p>Computes multi-dimensional review analytics without an external NLP service —
 * all signals are derived from the pre-computed fields stored in the {@link Review} entity
 * (sentimentScore, aspectCluster, authenticityFlag) that were populated during the data
 * seeding / future scraping pipeline.</p>
 */
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ProductReviewsDto getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findAllByProductId(productId);

        if (reviews.isEmpty()) {
            return emptyReviews(productId);
        }

        // ── Rating distribution ──────────────────────────────────────
        Map<Integer, Long> ratingDist = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) ratingDist.put(i, 0L);
        reviewRepository.countByRatingForProduct(productId)
            .forEach(row -> ratingDist.put((Integer) row[0], (Long) row[1]));

        double avgRating = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        // ── Aspect clusters ──────────────────────────────────────────
        Map<String, List<Review>> byCluster = reviews.stream()
            .filter(r -> r.getAspectCluster() != null)
            .collect(Collectors.groupingBy(Review::getAspectCluster));

        List<AspectClusterDto> aspectClusters = byCluster.entrySet().stream()
            .sorted(Map.Entry.<String, List<Review>>comparingByValue(
                Comparator.comparingInt(List::size)).reversed())
            .map(e -> buildCluster(e.getKey(), e.getValue()))
            .collect(Collectors.toList());

        // ── Authenticity signals ─────────────────────────────────────
        AuthenticityDto authenticity = buildAuthenticity(reviews);

        // ── Buy verdict ──────────────────────────────────────────────
        String[] verdict = computeBuyVerdict(avgRating, authenticity, reviews);

        // ── Top reviews ──────────────────────────────────────────────
        List<ReviewItemDto> topPositive = reviews.stream()
            .filter(r -> r.getRating() >= 4)
            .sorted(Comparator.comparingInt(Review::getHelpfulVotes).reversed())
            .limit(5)
            .map(this::toDto)
            .collect(Collectors.toList());

        List<ReviewItemDto> topCritical = reviews.stream()
            .filter(r -> r.getRating() <= 2)
            .sorted(Comparator.comparingInt(Review::getHelpfulVotes).reversed())
            .limit(5)
            .map(this::toDto)
            .collect(Collectors.toList());

        List<ReviewItemDto> allReviews = reviews.stream()
            .limit(50)
            .map(this::toDto)
            .collect(Collectors.toList());

        return new ProductReviewsDto(
            productId,
            reviews.size(),
            Math.round(avgRating * 10.0) / 10.0,
            ratingDist,
            aspectClusters,
            authenticity,
            verdict[0],
            verdict[1],
            topPositive,
            topCritical,
            allReviews
        );
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private AspectClusterDto buildCluster(String name, List<Review> clusterReviews) {
        double avgSentiment = clusterReviews.stream()
            .filter(r -> r.getSentimentScore() != null)
            .mapToDouble(Review::getSentimentScore)
            .average()
            .orElse(0.0);

        double avgRating = clusterReviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        String sentiment = avgSentiment >= 0.25 ? "PRIMARY_POSITIVE"
            : avgSentiment <= -0.25 ? "PRIMARY_NEGATIVE"
            : "MIXED";

        return new AspectClusterDto(
            name,
            clusterReviews.size(),
            Math.round(avgSentiment * 100.0) / 100.0,
            Math.round(avgRating * 10.0) / 10.0,
            sentiment
        );
    }

    private AuthenticityDto buildAuthenticity(List<Review> reviews) {
        long total = reviews.size();
        long suspicious = reviews.stream()
            .filter(r -> "SUSPICIOUS_BURST".equals(r.getAuthenticityFlag())).count();
        long bot = reviews.stream()
            .filter(r -> "BOT_PATTERN".equals(r.getAuthenticityFlag())).count();
        long spike = reviews.stream()
            .filter(r -> "UNVERIFIED_SPIKE".equals(r.getAuthenticityFlag())).count();
        long normal = total - suspicious - bot - spike;

        double cleanPct = total > 0 ? Math.round((normal * 100.0 / total) * 10.0) / 10.0 : 100.0;

        String health;
        List<String> warnings = new ArrayList<>();

        if (bot > 0 || suspicious >= 3) {
            health = "MAJOR_CONCERN";
        } else if (suspicious > 0 || spike > 0) {
            health = "MINOR_CONCERN";
        } else {
            health = "CLEAN";
        }

        if (suspicious > 0) warnings.add(suspicious + " review(s) show burst-posting patterns (many reviews in a short window).");
        if (bot > 0)        warnings.add(bot + " review(s) show bot-like patterns (template text, no purchase history).");
        if (spike > 0)      warnings.add(spike + " unverified-purchase review(s) posted during a rating spike.");

        String summary = switch (health) {
            case "CLEAN"         -> "Review patterns look genuine — no significant anomalies detected.";
            case "MINOR_CONCERN" -> "Minor anomalies detected. Most reviews appear authentic, but a small cluster warrants caution.";
            case "MAJOR_CONCERN" -> "Significant authenticity concerns detected. Treat the overall rating with caution.";
            default              -> "Analysis complete.";
        };

        return new AuthenticityDto(health, cleanPct, (int) suspicious, (int) bot, (int) spike, summary, warnings);
    }

    private String[] computeBuyVerdict(double avgRating, AuthenticityDto auth, List<Review> reviews) {
        // Check recent trend (last 10 reviews vs overall)
        double recentAvg = reviews.stream().limit(10)
            .mapToInt(Review::getRating).average().orElse(avgRating);
        boolean trendingUp = recentAvg > avgRating + 0.2;

        if ("MAJOR_CONCERN".equals(auth.overallHealth())) {
            return new String[]{"WAIT", "Review authenticity concerns make it hard to trust the rating. Wait for independent coverage."};
        }
        if (avgRating >= 4.3 && "CLEAN".equals(auth.overallHealth())) {
            return new String[]{"GREAT_TIME", "Highly rated with authentic reviews. " + (trendingUp ? "Recent reviews are even more positive." : "Solid consistent satisfaction.")};
        }
        if (avgRating < 3.5) {
            return new String[]{"WAIT", "Average rating is below 3.5 stars. Consider alternatives or wait for an improved version."};
        }
        return new String[]{"NEUTRAL", "Reviews are decent but mixed. Check the specific concerns highlighted below before purchasing."};
    }

    private ReviewItemDto toDto(Review r) {
        return new ReviewItemDto(
            r.getId(),
            r.getSourceName(),
            r.getReviewerName(),
            r.getRating(),
            r.getTitle(),
            r.getBody(),
            r.getReviewDate(),
            r.isVerifiedPurchase(),
            r.getHelpfulVotes(),
            r.getSentimentScore(),
            r.getAspectCluster(),
            r.getAuthenticityFlag()
        );
    }

    private ProductReviewsDto emptyReviews(Long productId) {
        return new ProductReviewsDto(
            productId, 0, 0.0,
            Map.of(5, 0L, 4, 0L, 3, 0L, 2, 0L, 1, 0L),
            List.of(),
            new AuthenticityDto("CLEAN", 100.0, 0, 0, 0, "No reviews found for this product.", List.of()),
            "NEUTRAL", "Not enough reviews to determine a reliable verdict.",
            List.of(), List.of(), List.of()
        );
    }
}
