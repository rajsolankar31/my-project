package com.productintel.review.entity;

import com.productintel.product.entity.ProductVariant;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A single user review for a product variant collected from a specific source store.
 * Reviews are immutable once persisted — new crawl passes insert new rows.
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_variant_id", columnList = "product_variant_id"),
    @Index(name = "idx_review_source",     columnList = "source_name"),
    @Index(name = "idx_review_date",       columnList = "review_date")
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    /** Which store this review was scraped from */
    @Column(name = "source_name", nullable = false, length = 60)
    private String sourceName;

    /** External reviewer display name (may be anonymised) */
    @Column(name = "reviewer_name", length = 120)
    private String reviewerName;

    /** Star rating 1–5 */
    @Column(nullable = false)
    private Integer rating;

    /** Short review headline */
    @Column(length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    /** Original date the review was written on the source platform */
    @Column(name = "review_date")
    private LocalDate reviewDate;

    /** Whether the purchase was verified on the source platform */
    @Column(name = "verified_purchase", nullable = false)
    private boolean verifiedPurchase = false;

    /** Helpful votes count from the source platform */
    @Column(name = "helpful_votes", nullable = false)
    private int helpfulVotes = 0;

    // ── Computed sentiment fields (populated after NLP pass) ──

    /**
     * Sentiment score: -1.0 (very negative) → +1.0 (very positive).
     * Null means not yet analysed.
     */
    @Column(name = "sentiment_score")
    private Double sentimentScore;

    /**
     * Cluster label assigned by the analysis pipeline.
     * Examples: "Build Quality", "Battery Life", "Noise Cancellation", "Value".
     */
    @Column(name = "aspect_cluster", length = 80)
    private String aspectCluster;

    /**
     * Authenticity flags: NORMAL | SUSPICIOUS_BURST | BOT_PATTERN | UNVERIFIED_SPIKE
     */
    @Column(name = "authenticity_flag", length = 40)
    private String authenticityFlag = "NORMAL";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Review() {}

    public Review(
        ProductVariant productVariant,
        String sourceName,
        String reviewerName,
        Integer rating,
        String title,
        String body,
        LocalDate reviewDate,
        boolean verifiedPurchase,
        int helpfulVotes,
        Double sentimentScore,
        String aspectCluster,
        String authenticityFlag
    ) {
        this.productVariant    = productVariant;
        this.sourceName        = sourceName;
        this.reviewerName      = reviewerName;
        this.rating            = rating;
        this.title             = title;
        this.body              = body;
        this.reviewDate        = reviewDate;
        this.verifiedPurchase  = verifiedPurchase;
        this.helpfulVotes      = helpfulVotes;
        this.sentimentScore    = sentimentScore;
        this.aspectCluster     = aspectCluster;
        this.authenticityFlag  = authenticityFlag != null ? authenticityFlag : "NORMAL";
    }

    // Getters
    public Long       getId()               { return id; }
    public ProductVariant getProductVariant(){ return productVariant; }
    public String     getSourceName()       { return sourceName; }
    public String     getReviewerName()     { return reviewerName; }
    public Integer    getRating()           { return rating; }
    public String     getTitle()            { return title; }
    public String     getBody()             { return body; }
    public LocalDate  getReviewDate()       { return reviewDate; }
    public boolean    isVerifiedPurchase()  { return verifiedPurchase; }
    public int        getHelpfulVotes()     { return helpfulVotes; }
    public Double     getSentimentScore()   { return sentimentScore; }
    public String     getAspectCluster()    { return aspectCluster; }
    public String     getAuthenticityFlag() { return authenticityFlag; }
    public Instant    getCreatedAt()        { return createdAt; }
}
