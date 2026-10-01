package com.productintel.price.service;

import com.productintel.listing.entity.Listing;
import com.productintel.listing.repository.ListingRepository;
import com.productintel.price.dto.ListingPriceDto;
import com.productintel.price.dto.PricePointDto;
import com.productintel.price.dto.PriceSummaryDto;
import com.productintel.price.dto.ProductPricesResponseDto;
import com.productintel.price.entity.PriceObservation;
import com.productintel.price.repository.PriceObservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PriceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        .withZone(ZoneId.systemDefault());

    private final PriceObservationRepository priceObservationRepository;
    private final ListingRepository listingRepository;

    public PriceService(PriceObservationRepository priceObservationRepository, ListingRepository listingRepository) {
        this.priceObservationRepository = priceObservationRepository;
        this.listingRepository = listingRepository;
    }

    public ProductPricesResponseDto getProductPrices(Long productId) {
        List<PriceObservation> observations = priceObservationRepository.findTimelineByProductId(productId);
        List<Listing> listings = listingRepository.findAllByProductId(productId);

        if (observations.isEmpty() && listings.isEmpty()) {
            return new ProductPricesResponseDto(productId, null, Collections.emptyList(), Collections.emptyList());
        }

        // 1. Build Listings with latest observed prices
        List<ListingPriceDto> listingDtos = new ArrayList<>();
        Double minCurrentPrice = null;

        // Map latest observation per listing
        Map<Long, PriceObservation> latestByListing = new HashMap<>();
        for (PriceObservation obs : observations) {
            Long lId = obs.getListing().getId();
            PriceObservation existing = latestByListing.get(lId);
            if (existing == null || obs.getObservedAt().isAfter(existing.getObservedAt())) {
                latestByListing.put(lId, obs);
            }
        }

        for (Listing l : listings) {
            PriceObservation latest = latestByListing.get(l.getId());
            Double price = latest != null ? latest.getPrice() : null;
            Double shipping = latest != null ? latest.getShippingCost() : 0.0;
            Double total = price != null ? price + shipping : null;

            if (price != null && (minCurrentPrice == null || price < minCurrentPrice)) {
                minCurrentPrice = price;
            }
        }

        final Double lowestPrice = minCurrentPrice;
        for (Listing l : listings) {
            PriceObservation latest = latestByListing.get(l.getId());
            Double price = latest != null ? latest.getPrice() : null;
            Double shipping = latest != null ? latest.getShippingCost() : 0.0;
            Double total = price != null ? price + shipping : null;
            boolean isLowest = price != null && lowestPrice != null && Math.abs(price - lowestPrice) < 0.01;

            listingDtos.add(new ListingPriceDto(
                l.getId(),
                l.getSource().getName(),
                l.getSellerName(),
                l.getTitle(),
                l.getProductUrl(),
                price,
                shipping,
                total,
                l.getAvailability(),
                latest != null ? latest.getObservedAt() : l.getLastSeenAt(),
                isLowest
            ));
        }

        // Sort listings by price ASC
        listingDtos.sort(Comparator.comparing(ListingPriceDto::price, Comparator.nullsLast(Double::compareTo)));

        // 2. Build Timeline
        List<PricePointDto> timeline = new ArrayList<>();
        List<Double> allPrices = new ArrayList<>();

        for (PriceObservation obs : observations) {
            timeline.add(new PricePointDto(
                obs.getObservedAt(),
                DATE_FORMATTER.format(obs.getObservedAt()),
                obs.getPrice(),
                obs.getListing().getSource().getName(),
                obs.getListing().getSellerName()
            ));
            allPrices.add(obs.getPrice());
        }

        // 3. Derived Summary Metrics
        PriceSummaryDto summary = null;
        if (!allPrices.isEmpty()) {
            Double lowestObserved = allPrices.stream().min(Double::compareTo).orElse(0.0);
            Double highestObserved = allPrices.stream().max(Double::compareTo).orElse(0.0);
            Double averageObserved = allPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            // Median calculation
            List<Double> sortedPrices = new ArrayList<>(allPrices);
            Collections.sort(sortedPrices);
            Double medianObserved;
            int size = sortedPrices.size();
            if (size % 2 == 0) {
                medianObserved = (sortedPrices.get(size / 2 - 1) + sortedPrices.get(size / 2)) / 2.0;
            } else {
                medianObserved = sortedPrices.get(size / 2);
            }

            Double currentObserved = lowestPrice != null ? lowestPrice : allPrices.get(allPrices.size() - 1);
            Double percentDiff = averageObserved > 0 ? ((currentObserved - averageObserved) / averageObserved) * 100 : 0.0;

            String position;
            if (Math.abs(currentObserved - lowestObserved) < 0.01) {
                position = "AT_LOWEST";
            } else if (currentObserved < averageObserved * 0.97) {
                position = "BELOW_AVERAGE";
            } else if (currentObserved > averageObserved * 1.03) {
                position = "ABOVE_AVERAGE";
            } else {
                position = "NEAR_AVERAGE";
            }

            PriceObservation lastObs = observations.get(observations.size() - 1);

            summary = new PriceSummaryDto(
                currentObserved,
                lowestObserved,
                highestObserved,
                Math.round(averageObserved * 100.0) / 100.0,
                Math.round(medianObserved * 100.0) / 100.0,
                "INR",
                allPrices.size(),
                position,
                Math.round(percentDiff * 10.0) / 10.0,
                lastObs.getObservedAt()
            );
        }

        return new ProductPricesResponseDto(productId, summary, timeline, listingDtos);
    }
}
