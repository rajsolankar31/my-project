package com.productintel.data;

import com.productintel.category.entity.Category;
import com.productintel.category.repository.CategoryRepository;
import com.productintel.listing.entity.Listing;
import com.productintel.listing.repository.ListingRepository;
import com.productintel.price.entity.PriceObservation;
import com.productintel.product.entity.Product;
import com.productintel.product.entity.ProductVariant;
import com.productintel.product.repository.ProductRepository;
import com.productintel.source.entity.Source;
import com.productintel.source.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SourceRepository sourceRepository;
    private final ListingRepository listingRepository;

    public DataSeeder(
        CategoryRepository categoryRepository,
        ProductRepository productRepository,
        SourceRepository sourceRepository,
        ListingRepository listingRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.sourceRepository = sourceRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Database already seeded with products. Skipping.");
            return;
        }

        log.info("Seeding canonical categories, products, sources, listings, and price observations...");

        // 1. Categories
        Category headphones = categoryRepository.save(new Category("Headphones", "headphones"));
        Category smartphones = categoryRepository.save(new Category("Smartphones", "smartphones"));
        Category laptops = categoryRepository.save(new Category("Laptops", "laptops"));

        // 2. Sources
        Source amazon = sourceRepository.save(new Source("Amazon India", "https://www.amazon.in", "E_COMMERCE"));
        Source flipkart = sourceRepository.save(new Source("Flipkart", "https://www.flipkart.com", "E_COMMERCE"));
        Source croma = sourceRepository.save(new Source("Croma", "https://www.croma.com", "OMNICHANNEL"));
        Source reliance = sourceRepository.save(new Source("Reliance Digital", "https://www.reliancedigital.in", "OMNICHANNEL"));

        Instant now = Instant.now();

        // 3. Product 1: Sony WH-1000XM5
        Product sonyXM5 = new Product(
            "Sony",
            "Sony WH-1000XM5 Wireless Noise Canceling Headphones",
            "WH-1000XM5",
            headphones,
            "Industry-leading noise canceling with two processors and eight microphones for unprecedented noise cancellation and exceptional call quality."
        );
        ProductVariant xm5Black = new ProductVariant(
            sonyXM5,
            "WH1000XM5-BLK",
            "WH-1000XM5/B",
            "Black",
            null,
            "Over-ear",
            "{\"batteryLife\":\"30 hours\",\"connectivity\":\"Bluetooth 5.2 / 3.5mm\",\"noiseCancellation\":\"Active Noise Cancellation\",\"driverSize\":\"30mm\",\"weight\":\"250g\"}",
            29990.0
        );
        ProductVariant xm5Silver = new ProductVariant(
            sonyXM5,
            "WH1000XM5-SLV",
            "WH-1000XM5/S",
            "Silver",
            null,
            "Over-ear",
            "{\"batteryLife\":\"30 hours\",\"connectivity\":\"Bluetooth 5.2 / 3.5mm\",\"noiseCancellation\":\"Active Noise Cancellation\",\"driverSize\":\"30mm\",\"weight\":\"250g\"}",
            29990.0
        );
        sonyXM5.addVariant(xm5Black);
        sonyXM5.addVariant(xm5Silver);
        productRepository.save(sonyXM5);

        // Listings & Price Timeline for Sony XM5 Black
        Listing xm5Amazon = new Listing(xm5Black, amazon, "B09XS7JWHH", "Appario Retail", "Sony WH-1000XM5 Wireless Noise Cancelling Headphones - Black", "https://www.amazon.in/dp/B09XS7JWHH", "IN_STOCK");
        xm5Amazon.addPriceObservation(new PriceObservation(xm5Amazon, 33990.0, "INR", 0.0, "IN_STOCK", now.minus(90, ChronoUnit.DAYS)));
        xm5Amazon.addPriceObservation(new PriceObservation(xm5Amazon, 31990.0, "INR", 0.0, "IN_STOCK", now.minus(60, ChronoUnit.DAYS)));
        xm5Amazon.addPriceObservation(new PriceObservation(xm5Amazon, 29990.0, "INR", 0.0, "IN_STOCK", now.minus(30, ChronoUnit.DAYS)));
        xm5Amazon.addPriceObservation(new PriceObservation(xm5Amazon, 26990.0, "INR", 0.0, "IN_STOCK", now.minus(14, ChronoUnit.DAYS)));
        xm5Amazon.addPriceObservation(new PriceObservation(xm5Amazon, 28499.0, "INR", 0.0, "IN_STOCK", now.minus(1, ChronoUnit.DAYS)));
        listingRepository.save(xm5Amazon);

        Listing xm5Flipkart = new Listing(xm5Black, flipkart, "ITM82918829", "IndiFlashMart", "SONY WH-1000XM5 Bluetooth Headset (Black)", "https://www.flipkart.com/sony-wh-1000xm5-wireless-headphones/p/ITM82918829", "IN_STOCK");
        xm5Flipkart.addPriceObservation(new PriceObservation(xm5Flipkart, 34990.0, "INR", 50.0, "IN_STOCK", now.minus(75, ChronoUnit.DAYS)));
        xm5Flipkart.addPriceObservation(new PriceObservation(xm5Flipkart, 32490.0, "INR", 0.0, "IN_STOCK", now.minus(40, ChronoUnit.DAYS)));
        xm5Flipkart.addPriceObservation(new PriceObservation(xm5Flipkart, 29990.0, "INR", 0.0, "IN_STOCK", now.minus(10, ChronoUnit.DAYS)));
        xm5Flipkart.addPriceObservation(new PriceObservation(xm5Flipkart, 29490.0, "INR", 0.0, "IN_STOCK", now.minus(2, ChronoUnit.DAYS)));
        listingRepository.save(xm5Flipkart);

        Listing xm5Croma = new Listing(xm5Black, croma, "254891", "Croma Online", "Sony WH-1000XM5 Active Noise Cancellation Wireless Headphone (Black)", "https://www.croma.com/sony-wh-1000xm5-wireless-over-ear-noise-canceling-headphone/p/254891", "IN_STOCK");
        xm5Croma.addPriceObservation(new PriceObservation(xm5Croma, 34990.0, "INR", 0.0, "IN_STOCK", now.minus(80, ChronoUnit.DAYS)));
        xm5Croma.addPriceObservation(new PriceObservation(xm5Croma, 31990.0, "INR", 0.0, "IN_STOCK", now.minus(25, ChronoUnit.DAYS)));
        xm5Croma.addPriceObservation(new PriceObservation(xm5Croma, 30990.0, "INR", 0.0, "IN_STOCK", now.minus(3, ChronoUnit.DAYS)));
        listingRepository.save(xm5Croma);

        // 4. Product 2: Apple iPhone 16
        Product iphone16 = new Product(
            "Apple",
            "Apple iPhone 16",
            "iPhone 16",
            smartphones,
            "Featuring Camera Control, 48MP Fusion camera with 2x Telephoto, 5-core GPU with Apple Intelligence, and A18 chip."
        );
        ProductVariant iphone16Black128 = new ProductVariant(
            iphone16,
            "MYEE3HN/A",
            "A3287",
            "Black",
            "128 GB",
            "6.1-inch",
            "{\"display\":\"6.1 Super Retina XDR OLED\",\"chip\":\"A18 Bionic\",\"mainCamera\":\"48MP + 12MP\",\"battery\":\"Up to 22h video\"}",
            79900.0
        );
        ProductVariant iphone16Ultramarine = new ProductVariant(
            iphone16,
            "MYEF3HN/A",
            "A3287",
            "Ultramarine",
            "128 GB",
            "6.1-inch",
            "{\"display\":\"6.1 Super Retina XDR OLED\",\"chip\":\"A18 Bionic\",\"mainCamera\":\"48MP + 12MP\",\"battery\":\"Up to 22h video\"}",
            79900.0
        );
        iphone16.addVariant(iphone16Black128);
        iphone16.addVariant(iphone16Ultramarine);
        productRepository.save(iphone16);

        Listing ip16Amazon = new Listing(iphone16Black128, amazon, "B0DGJ9M8XY", "Apple Authorized Reseller", "Apple iPhone 16 (128 GB) - Black", "https://www.amazon.in/dp/B0DGJ9M8XY", "IN_STOCK");
        ip16Amazon.addPriceObservation(new PriceObservation(ip16Amazon, 79900.0, "INR", 0.0, "IN_STOCK", now.minus(60, ChronoUnit.DAYS)));
        ip16Amazon.addPriceObservation(new PriceObservation(ip16Amazon, 78900.0, "INR", 0.0, "IN_STOCK", now.minus(30, ChronoUnit.DAYS)));
        ip16Amazon.addPriceObservation(new PriceObservation(ip16Amazon, 76990.0, "INR", 0.0, "IN_STOCK", now.minus(2, ChronoUnit.DAYS)));
        listingRepository.save(ip16Amazon);

        Listing ip16Reliance = new Listing(iphone16Black128, reliance, "494421980", "Reliance Digital Official", "Apple iPhone 16 128 GB Black", "https://www.reliancedigital.in/apple-iphone-16-128-gb-black/p/494421980", "IN_STOCK");
        ip16Reliance.addPriceObservation(new PriceObservation(ip16Reliance, 79900.0, "INR", 0.0, "IN_STOCK", now.minus(45, ChronoUnit.DAYS)));
        ip16Reliance.addPriceObservation(new PriceObservation(ip16Reliance, 77490.0, "INR", 0.0, "IN_STOCK", now.minus(5, ChronoUnit.DAYS)));
        listingRepository.save(ip16Reliance);

        Listing ip16Flipkart = new Listing(iphone16Black128, flipkart, "MOBH4VYFHHWMPBSA", "Flipkart Apple Store", "Apple iPhone 16 (Black, 128 GB)", "https://www.flipkart.com/apple-iphone-16-black-128-gb/p/MOBH4VYFHHWMPBSA", "IN_STOCK");
        ip16Flipkart.addPriceObservation(new PriceObservation(ip16Flipkart, 79900.0, "INR", 0.0, "IN_STOCK", now.minus(55, ChronoUnit.DAYS)));
        ip16Flipkart.addPriceObservation(new PriceObservation(ip16Flipkart, 77999.0, "INR", 0.0, "IN_STOCK", now.minus(20, ChronoUnit.DAYS)));
        ip16Flipkart.addPriceObservation(new PriceObservation(ip16Flipkart, 75999.0, "INR", 500.0, "IN_STOCK", now.minus(1, ChronoUnit.DAYS)));
        listingRepository.save(ip16Flipkart);

        // 5. Product 3: Apple MacBook Pro 14"
        Product macbookPro = new Product(
            "Apple",
            "Apple MacBook Pro 14-inch (M3)",
            "MacBook Pro 14",
            laptops,
            "Supercharged by the M3 chip with an 8-core CPU and 10-core GPU, Liquid Retina XDR display, and up to 22 hours of battery life."
        );
        ProductVariant mbpM3 = new ProductVariant(
            macbookPro,
            "MTL73HN/A",
            "A2918",
            "Space Gray",
            "512 GB SSD",
            "14.2-inch",
            "{\"processor\":\"Apple M3 (8-core CPU, 10-core GPU)\",\"ram\":\"16 GB Unified\",\"storage\":\"512 GB SSD\",\"display\":\"Liquid Retina XDR (3024x1964)\",\"battery\":\"70Wh up to 22 hours\"}",
            169900.0
        );
        macbookPro.addVariant(mbpM3);
        productRepository.save(macbookPro);

        Listing mbpAmazon = new Listing(mbpM3, amazon, "B0CM5JSW84", "Appario Retail", "Apple 2023 MacBook Pro (14-inch, M3 chip, 16GB, 512GB) - Space Grey", "https://www.amazon.in/dp/B0CM5JSW84", "IN_STOCK");
        mbpAmazon.addPriceObservation(new PriceObservation(mbpAmazon, 169900.0, "INR", 0.0, "IN_STOCK", now.minus(90, ChronoUnit.DAYS)));
        mbpAmazon.addPriceObservation(new PriceObservation(mbpAmazon, 159990.0, "INR", 0.0, "IN_STOCK", now.minus(40, ChronoUnit.DAYS)));
        mbpAmazon.addPriceObservation(new PriceObservation(mbpAmazon, 154900.0, "INR", 0.0, "IN_STOCK", now.minus(3, ChronoUnit.DAYS)));
        listingRepository.save(mbpAmazon);

        Listing mbpCroma = new Listing(mbpM3, croma, "302781", "Croma Online", "Apple MacBook Pro 14 (M3, 16GB RAM, 512GB SSD) Space Grey", "https://www.croma.com/apple-macbook-pro-14-inch-m3-chip-16gb-512gb-space-grey/p/302781", "IN_STOCK");
        mbpCroma.addPriceObservation(new PriceObservation(mbpCroma, 169900.0, "INR", 0.0, "IN_STOCK", now.minus(70, ChronoUnit.DAYS)));
        mbpCroma.addPriceObservation(new PriceObservation(mbpCroma, 158900.0, "INR", 0.0, "IN_STOCK", now.minus(15, ChronoUnit.DAYS)));
        listingRepository.save(mbpCroma);

        Listing mbpReliance = new Listing(mbpM3, reliance, "530142376", "Reliance Digital Official", "Apple MacBook Pro 14-inch M3 16GB 512GB Space Grey", "https://www.reliancedigital.in/apple-macbook-pro-14-m3-16gb-512gb/p/530142376", "IN_STOCK");
        mbpReliance.addPriceObservation(new PriceObservation(mbpReliance, 169900.0, "INR", 0.0, "IN_STOCK", now.minus(60, ChronoUnit.DAYS)));
        mbpReliance.addPriceObservation(new PriceObservation(mbpReliance, 162900.0, "INR", 0.0, "IN_STOCK", now.minus(10, ChronoUnit.DAYS)));
        listingRepository.save(mbpReliance);

        // 6. Product 4: Bose QuietComfort 45
        Product boseQC45 = new Product(
            "Bose",
            "Bose QuietComfort 45 Bluetooth Wireless Headphones",
            "QC45",
            headphones,
            "High-fidelity audio with proprietary acoustic technology, Quiet and Aware modes, and comfortable lightweight design."
        );
        ProductVariant boseBlack = new ProductVariant(
            boseQC45,
            "BOSE-QC45-BLK",
            "866724-0100",
            "Triple Black",
            null,
            "Over-ear",
            "{\"batteryLife\":\"24 hours\",\"connectivity\":\"Bluetooth 5.1\",\"noiseCancellation\":\"Active (Quiet/Aware)\",\"weight\":\"240g\"}",
            24999.0
        );
        boseQC45.addVariant(boseBlack);
        productRepository.save(boseQC45);

        Listing boseAmazon = new Listing(boseBlack, amazon, "B098FKXT8L", "Bose Store", "Bose QuietComfort 45 Bluetooth Wireless Headphones - Triple Black", "https://www.amazon.in/dp/B098FKXT8L", "IN_STOCK");
        boseAmazon.addPriceObservation(new PriceObservation(boseAmazon, 29900.0, "INR", 0.0, "IN_STOCK", now.minus(90, ChronoUnit.DAYS)));
        boseAmazon.addPriceObservation(new PriceObservation(boseAmazon, 26900.0, "INR", 0.0, "IN_STOCK", now.minus(50, ChronoUnit.DAYS)));
        boseAmazon.addPriceObservation(new PriceObservation(boseAmazon, 23999.0, "INR", 0.0, "IN_STOCK", now.minus(4, ChronoUnit.DAYS)));
        listingRepository.save(boseAmazon);

        Listing boseCroma = new Listing(boseBlack, croma, "231831", "Croma Online", "Bose QuietComfort 45 Wireless Over Ear Headphones (Triple Black)", "https://www.croma.com/bose-quietcomfort-45-wireless-over-ear-headphones/p/231831", "IN_STOCK");
        boseCroma.addPriceObservation(new PriceObservation(boseCroma, 30990.0, "INR", 0.0, "IN_STOCK", now.minus(85, ChronoUnit.DAYS)));
        boseCroma.addPriceObservation(new PriceObservation(boseCroma, 27490.0, "INR", 0.0, "IN_STOCK", now.minus(35, ChronoUnit.DAYS)));
        boseCroma.addPriceObservation(new PriceObservation(boseCroma, 24490.0, "INR", 0.0, "IN_STOCK", now.minus(5, ChronoUnit.DAYS)));
        listingRepository.save(boseCroma);

        Listing boseFlipkart = new Listing(boseBlack, flipkart, "HDPHBG5Z6YHBGBBF", "RetailNet", "Bose QuietComfort 45 Bluetooth Headphones Triple Black", "https://www.flipkart.com/bose-quietcomfort-45-bluetooth-headphones/p/HDPHBG5Z6YHBGBBF", "IN_STOCK");
        boseFlipkart.addPriceObservation(new PriceObservation(boseFlipkart, 31900.0, "INR", 0.0, "IN_STOCK", now.minus(75, ChronoUnit.DAYS)));
        boseFlipkart.addPriceObservation(new PriceObservation(boseFlipkart, 26999.0, "INR", 0.0, "IN_STOCK", now.minus(28, ChronoUnit.DAYS)));
        boseFlipkart.addPriceObservation(new PriceObservation(boseFlipkart, 24999.0, "INR", 0.0, "IN_STOCK", now.minus(3, ChronoUnit.DAYS)));
        listingRepository.save(boseFlipkart);

        // 7. Product 5: Dell XPS 15
        Product dellXps = new Product(
            "Dell",
            "Dell XPS 15 Laptop (9530)",
            "XPS 15 9530",
            laptops,
            "High-performance creator laptop powered by 13th Gen Intel Core i7, NVIDIA GeForce RTX graphics, and immersive InfinityEdge display."
        );
        ProductVariant xpsSilver = new ProductVariant(
            dellXps,
            "XPS9530-7458SLV",
            "9530",
            "Platinum Silver",
            "1 TB SSD",
            "15.6-inch",
            "{\"processor\":\"Intel Core i7-13700H\",\"ram\":\"16 GB DDR5\",\"storage\":\"1 TB NVMe SSD\",\"graphics\":\"NVIDIA RTX 4050 6GB\",\"display\":\"15.6 FHD+ (1920x1200)\"}",
            154990.0
        );
        dellXps.addVariant(xpsSilver);
        productRepository.save(dellXps);

        Listing dellAmazon = new Listing(xpsSilver, amazon, "B0C6DP9F92", "Cloudtail India", "Dell XPS 9530 15.6 inch FHD+ Laptop (Core i7-13700H / 16GB / 1TB SSD / RTX 4050)", "https://www.amazon.in/dp/B0C6DP9F92", "IN_STOCK");
        dellAmazon.addPriceObservation(new PriceObservation(dellAmazon, 169990.0, "INR", 0.0, "IN_STOCK", now.minus(90, ChronoUnit.DAYS)));
        dellAmazon.addPriceObservation(new PriceObservation(dellAmazon, 159990.0, "INR", 0.0, "IN_STOCK", now.minus(45, ChronoUnit.DAYS)));
        dellAmazon.addPriceObservation(new PriceObservation(dellAmazon, 154990.0, "INR", 0.0, "IN_STOCK", now.minus(5, ChronoUnit.DAYS)));
        listingRepository.save(dellAmazon);

        Listing dellReliance = new Listing(xpsSilver, reliance, "491892201", "Reliance Digital Official", "Dell XPS 15 9530 i7 16GB 1TB RTX4050 Platinum Silver", "https://www.reliancedigital.in/dell-xps-15-9530-laptop/p/491892201", "IN_STOCK");
        dellReliance.addPriceObservation(new PriceObservation(dellReliance, 169900.0, "INR", 0.0, "IN_STOCK", now.minus(80, ChronoUnit.DAYS)));
        dellReliance.addPriceObservation(new PriceObservation(dellReliance, 157990.0, "INR", 0.0, "IN_STOCK", now.minus(22, ChronoUnit.DAYS)));
        listingRepository.save(dellReliance);

        log.info("Seeding completed successfully: {} products and multiple multi-source listings created.", productRepository.count());
    }
}
