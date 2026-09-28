package com.retailx.inventory.config;

import com.retailx.inventory.dto.CreateStockMovementRequest;
import com.retailx.inventory.entity.MovementType;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.service.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepository;
    private final StockMovementService stockMovementService;

    public DataInitializer(ProductRepository productRepository,
                           StockMovementService stockMovementService) {
        this.productRepository = productRepository;
        this.stockMovementService = stockMovementService;
    }

    private static class ProductSeed {
        String name;
        String sku;
        int threshold;
        int reorderQty;
        int purchase;
        int sales;
        int returns;
        int damage;

        ProductSeed(String name, String sku, int threshold, int reorderQty, int purchase, int sales, int returns, int damage) {
            this.name = name;
            this.sku = sku;
            this.threshold = threshold;
            this.reorderQty = reorderQty;
            this.purchase = purchase;
            this.sales = sales;
            this.returns = returns;
            this.damage = damage;
        }
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Seeding large realistic retail catalog (94 products) and stock movements...");

            List<ProductSeed> catalog = new ArrayList<>();

            // 1. BEVERAGES
            catalog.add(new ProductSeed("Coca-Cola 500ml", "COKE500", 20, 100, 180, 140, 2, 3)); // Fast moving, stock 39
            catalog.add(new ProductSeed("Pepsi 500ml", "PEPSI500", 20, 100, 120, 75, 0, 1));
            catalog.add(new ProductSeed("Sprite 500ml", "SPRITE500", 15, 80, 100, 60, 0, 0));
            catalog.add(new ProductSeed("Fanta 500ml", "FANTA500", 15, 80, 90, 50, 1, 0));
            catalog.add(new ProductSeed("Maaza Mango 600ml", "MAAZA600", 15, 75, 110, 70, 0, 2));
            catalog.add(new ProductSeed("Thums Up 750ml", "THUMSUP750", 20, 100, 130, 95, 0, 1));
            catalog.add(new ProductSeed("Real Fruit Juice 1L", "REALJUICE1L", 12, 60, 70, 62, 0, 0)); // LOW STOCK: 8 <= 12 (ALERT)
            catalog.add(new ProductSeed("Bisleri Mineral Water 1L", "BISLERI1L", 30, 150, 200, 130, 0, 0)); // Fast moving

            // 2. BISCUITS & SNACKS
            catalog.add(new ProductSeed("Parle-G 800g", "PARLEG800", 15, 80, 100, 85, 0, 0)); // LOW STOCK: 15 <= 15 (ALERT)
            catalog.add(new ProductSeed("Britannia Good Day 200g", "GOODDAY200", 15, 60, 90, 55, 0, 1));
            catalog.add(new ProductSeed("Oreo Cookies 120g", "OREO120", 12, 50, 80, 48, 2, 0));
            catalog.add(new ProductSeed("Britannia Bourbon 150g", "BOURBON150", 10, 50, 70, 40, 0, 0));
            catalog.add(new ProductSeed("Sunfeast Dark Fantasy 150g", "DARKFANTASY150", 10, 40, 60, 35, 0, 1));
            catalog.add(new ProductSeed("Bingo Mad Angles 90g", "BINGO90", 15, 60, 85, 50, 0, 0));
            catalog.add(new ProductSeed("Lays Classic Salted 50g", "LAYS50", 25, 100, 150, 75, 0, 0)); // Fast moving
            catalog.add(new ProductSeed("Kurkure Masala Munch 90g", "KURKURE90", 20, 90, 110, 65, 0, 1));
            catalog.add(new ProductSeed("Haldiram's Bhujia 200g", "HBHUJIA200", 12, 50, 75, 45, 1, 0));
            catalog.add(new ProductSeed("Too Yumm Veggie Stix 60g", "TOOYUMM60", 10, 40, 50, 42, 0, 0)); // LOW STOCK: 8 <= 10 (ALERT)

            // 3. RICE & GRAINS
            catalog.add(new ProductSeed("India Gate Basmati Rice 5kg", "INGATE5KG", 10, 40, 60, 35, 0, 0));
            catalog.add(new ProductSeed("Aashirvaad Superior Rice 5kg", "AARRICE5", 12, 60, 120, 115, 0, 0)); // LOW STOCK: 5 <= 12 (ALERT) & Fast moving
            catalog.add(new ProductSeed("Fortune Special Basmati Rice 5kg", "FORTBAS5KG", 8, 35, 50, 30, 0, 0));
            catalog.add(new ProductSeed("Sona Masoori Raw Rice 5kg", "SONAMAS5KG", 15, 50, 80, 50, 0, 0));
            catalog.add(new ProductSeed("Tata Sampann Toor Dal 1kg", "TOORDAL1KG", 10, 40, 70, 45, 0, 1));
            catalog.add(new ProductSeed("Tata Sampann Moong Dal 1kg", "MOONGDAL1KG", 8, 35, 50, 30, 0, 0));
            catalog.add(new ProductSeed("Chana Dal 1kg", "CHANADAL1KG", 8, 30, 45, 38, 0, 0)); // LOW STOCK: 7 <= 8 (ALERT)
            catalog.add(new ProductSeed("Urad Dal White 1kg", "URADDAL1KG", 8, 30, 40, 25, 1, 0));
            catalog.add(new ProductSeed("Premium Red Rajma 1kg", "RAJMA1KG", 6, 25, 35, 20, 0, 0));

            // 4. COOKING ESSENTIALS
            catalog.add(new ProductSeed("Fortune Sunflower Oil 1L", "FORTOIL1L", 15, 60, 100, 92, 0, 0)); // LOW STOCK: 8 <= 15 (ALERT) & Fast moving
            catalog.add(new ProductSeed("Gold Winner Refined Oil 1L", "GOLDWIN1L", 12, 50, 80, 50, 0, 1));
            catalog.add(new ProductSeed("Aashirvaad Shudh Chakki Atta 5kg", "AARATTA5KG", 15, 60, 110, 65, 0, 0));
            catalog.add(new ProductSeed("Tata Salt Vacuum Evaporated 1kg", "TATASALT1KG", 20, 100, 150, 90, 0, 0));
            catalog.add(new ProductSeed("Madhur Pure Refined Sugar 1kg", "SUGAR1KG", 20, 80, 120, 70, 0, 0));
            catalog.add(new ProductSeed("Everest Turmeric Powder 200g", "EVTURM200", 10, 40, 60, 35, 0, 0));
            catalog.add(new ProductSeed("Everest Chilli Powder 200g", "EVCHILLI200", 10, 40, 60, 38, 0, 0));
            catalog.add(new ProductSeed("Catch Cumin Seeds 100g", "JEERA100", 8, 30, 40, 22, 1, 0));
            catalog.add(new ProductSeed("Catch Mustard Seeds 100g", "MUSTARD100", 8, 30, 40, 20, 0, 0));
            catalog.add(new ProductSeed("Red Label Tea Powder 500g", "REDLABEL500", 12, 50, 80, 52, 0, 0));
            catalog.add(new ProductSeed("Nescafe Classic Instant Coffee 200g", "NESCAFE200", 10, 40, 50, 42, 0, 0)); // LOW STOCK: 8 <= 10 (ALERT)

            // 5. PERSONAL CARE
            catalog.add(new ProductSeed("Dove Cream Beauty Bathing Bar", "DOVE100", 12, 50, 80, 45, 0, 1));
            catalog.add(new ProductSeed("Lux Rose Soap 125g", "LUX125", 10, 50, 80, 20, 0, 0));
            catalog.add(new ProductSeed("Lifebuoy Total Soap 125g", "LIFEBUOY125", 15, 60, 90, 55, 0, 0));
            catalog.add(new ProductSeed("Pears Pure & Gentle Soap", "PEARS125", 10, 40, 60, 35, 1, 0));
            catalog.add(new ProductSeed("Colgate Strong Teeth Toothpaste 200g", "COLGATE200", 15, 60, 80, 68, 0, 0)); // LOW STOCK: 12 <= 15 (ALERT)
            catalog.add(new ProductSeed("Closeup Red Hot Toothpaste 150g", "CLOSEUP150", 12, 50, 70, 40, 0, 0));
            catalog.add(new ProductSeed("Head & Shoulders Shampoo 200ml", "HS200", 8, 40, 50, 33, 0, 0));
            catalog.add(new ProductSeed("Clinic Plus Strong & Long Shampoo 340ml", "CLINIC340", 10, 50, 65, 35, 0, 1));
            catalog.add(new ProductSeed("Sunsilk Black Shine Shampoo 350ml", "SUNSILK350", 10, 40, 55, 30, 0, 0));
            catalog.add(new ProductSeed("Parachute Pure Coconut Oil 500ml", "PARACHUTE500", 12, 50, 75, 45, 0, 0));
            catalog.add(new ProductSeed("Nivea Nourishing Body Lotion 400ml", "NIVEA400", 8, 30, 45, 25, 0, 0));
            catalog.add(new ProductSeed("Vaseline Original Jelly 100g", "VASELINE100", 10, 40, 50, 30, 1, 0));

            // 6. HOME CLEANING
            catalog.add(new ProductSeed("Surf Excel Easy Wash Detergent 2kg", "SURFEXCEL2KG", 10, 40, 60, 52, 0, 0)); // LOW STOCK: 8 <= 10 (ALERT)
            catalog.add(new ProductSeed("Ariel Matic Top Load Detergent 2kg", "ARIEL2KG", 8, 35, 50, 30, 0, 1));
            catalog.add(new ProductSeed("Rin Detergent Bar 250g", "RINBAR250", 20, 80, 100, 60, 0, 0));
            catalog.add(new ProductSeed("Vim Dishwash Liquid Gel 500ml", "VIMGEL500", 15, 60, 90, 55, 0, 0));
            catalog.add(new ProductSeed("Harpic Power Plus Toilet Cleaner 1L", "HARPIC1L", 12, 50, 80, 48, 0, 0));
            catalog.add(new ProductSeed("Lizol Citrus Floor Cleaner 1L", "LIZOL1L", 12, 50, 75, 45, 0, 0));
            catalog.add(new ProductSeed("Domex Disinfectant Floor Cleaner 1L", "DOMEX1L", 10, 40, 55, 32, 0, 0));
            catalog.add(new ProductSeed("Colin Glass & Surface Cleaner 500ml", "COLIN500", 8, 30, 40, 22, 1, 0));
            catalog.add(new ProductSeed("Comfort Fabric Conditioner 860ml", "COMFORT860", 8, 30, 45, 28, 0, 0));
            catalog.add(new ProductSeed("Good Knight Activ+ Mosquito Refill 45ml", "GOODKNIGHT45", 15, 60, 90, 55, 0, 0));

            // 7. DAIRY & BREAKFAST
            catalog.add(new ProductSeed("Amul Taaza Homogenised Toned Milk 1L", "AMULMILK1L", 20, 100, 150, 135, 2, 2)); // LOW STOCK: 15 <= 20 (ALERT) & Fast moving
            catalog.add(new ProductSeed("Amul Pasteurized Salted Butter 500g", "AMULBUTTER500", 12, 50, 80, 60, 0, 0));
            catalog.add(new ProductSeed("Amul Processed Cheese Blocks 200g", "AMULCHEESE200", 10, 40, 60, 35, 0, 0));
            catalog.add(new ProductSeed("Amul Masti Dahi Tub 400g", "AMULCURD400", 15, 60, 80, 50, 0, 0));
            catalog.add(new ProductSeed("Kellogg's Original Corn Flakes 500g", "KELLOGGS500", 10, 40, 55, 30, 0, 0));
            catalog.add(new ProductSeed("Quaker Rolled White Oats 1kg", "QUAKEROATS1KG", 10, 40, 60, 35, 1, 0));
            catalog.add(new ProductSeed("Horlicks Classic Malt Drink 500g", "HORLICKS500", 8, 35, 45, 25, 0, 0));
            catalog.add(new ProductSeed("Complan Nutrition Drink Royale Chocolate 500g", "COMPLAN500", 8, 35, 45, 28, 0, 0));
            catalog.add(new ProductSeed("Nestle Sweetened Milkmaid 380g", "MILKMAID380", 8, 30, 40, 24, 0, 0));

            // 8. PACKAGED FOODS
            catalog.add(new ProductSeed("Del Monte Tomato Ketchup 950g", "DELKETCHUP950", 10, 40, 60, 38, 0, 0));
            catalog.add(new ProductSeed("Veeba Eggless Mayonnaise 250g", "VEEBAMAYO250", 8, 30, 45, 26, 0, 0));
            catalog.add(new ProductSeed("Knorr Classic Mixed Veg Soup 43g", "KNORRSOUP43", 15, 60, 70, 40, 0, 0));
            catalog.add(new ProductSeed("Hershey's Chocolate Syrup 623g", "HERSHEY623", 6, 25, 35, 20, 0, 0));
            catalog.add(new ProductSeed("Nutella Hazelnut Spread 350g", "NUTELLA350", 8, 30, 40, 25, 1, 0));

            // 9. HOUSEHOLD ITEMS
            catalog.add(new ProductSeed("Scotch-Brite Sponge Scrub Pad", "SCOTCHBRITE3P", 20, 80, 100, 60, 0, 0));
            catalog.add(new ProductSeed("Shalimar Garbage Bags 30 Pcs Medium", "GARBAGEBAG30", 15, 50, 70, 40, 0, 0));
            catalog.add(new ProductSeed("Hindalco Freshwrap Aluminium Foil 18m", "ALFOIL18M", 10, 40, 50, 30, 0, 0));
            catalog.add(new ProductSeed("Freshwrapp Cling Film 30m", "CLINGFILM30M", 8, 30, 40, 24, 0, 0));
            catalog.add(new ProductSeed("Origami Kitchen Paper Towels 2 Rolls", "PAPERTOWEL2R", 12, 40, 55, 32, 0, 0));
            catalog.add(new ProductSeed("Paseo Soft Facial Tissues 200 Pulls", "TISSUEBOX200", 15, 50, 65, 38, 1, 0));
            catalog.add(new ProductSeed("Duracell Ultra Alkaline Batteries AA 4-Pack", "DURACELLAA4", 12, 40, 60, 35, 0, 0));
            catalog.add(new ProductSeed("Duracell Ultra Alkaline Batteries AAA 4-Pack", "DURACELLAAA4", 12, 40, 55, 48, 0, 0)); // LOW STOCK: 7 <= 12 (ALERT)

            // 10. STATIONERY
            catalog.add(new ProductSeed("Classmate A4 Ruled Spiral Notebook 300p", "CLASSNOTE300", 20, 80, 100, 60, 5, 0));
            catalog.add(new ProductSeed("Reynolds 045 Fine Ball Pen Blue Pack 5", "REYNOLDSBL5", 25, 100, 150, 80, 0, 0));
            catalog.add(new ProductSeed("Reynolds 045 Fine Ball Pen Black Pack 5", "REYNOLDSBK5", 20, 80, 100, 55, 0, 0));
            catalog.add(new ProductSeed("Apsara Platinum Extra Dark Pencils Pack 10", "APSARAPEN10", 15, 60, 80, 45, 0, 0));
            catalog.add(new ProductSeed("Camlin Exam Eraser Big Pack 5", "CAMLINERASER5", 20, 80, 90, 50, 0, 0));
            catalog.add(new ProductSeed("Faber-Castell Triangular Sharpener Pack 5", "SHARPENER5", 15, 60, 70, 40, 0, 0));
            catalog.add(new ProductSeed("Camlin Permanent Marker Black", "MARKERBK", 12, 50, 60, 35, 0, 0));
            catalog.add(new ProductSeed("Faber-Castell Textliner Highlighter Pack 5", "HIGHLIGHT5", 10, 40, 50, 30, 0, 0));
            catalog.add(new ProductSeed("Fevistik Super Glue Stick 15g", "FEVISTIK15", 15, 60, 75, 42, 0, 0));
            catalog.add(new ProductSeed("JK Copier A4 Copier Paper 75GSM 500 Sheets", "JKCOPIER500", 10, 40, 50, 30, 0, 0));

            // 11. BABY CARE
            catalog.add(new ProductSeed("Pampers All Round Protection Diapers M 44 Pcs", "PAMPERSM44", 10, 40, 50, 30, 0, 0));
            catalog.add(new ProductSeed("Huggies Wonder Pants Diaper Large 34 Pcs", "HUGGIESL34", 10, 40, 50, 32, 0, 0));
            catalog.add(new ProductSeed("Johnson's Baby Nourishing Soap 75g", "JHBABYSOAP75", 15, 60, 70, 40, 0, 0));
            catalog.add(new ProductSeed("Johnson's No More Tears Baby Shampoo 200ml", "JHBABYSHAMP200", 10, 40, 50, 28, 0, 0));
            catalog.add(new ProductSeed("Himalaya Gentle Baby Wipes 72 Pcs", "HIMBABYWIPES72", 15, 50, 65, 38, 0, 0));
            catalog.add(new ProductSeed("Johnson's Baby Powder 200g", "JHBABYPOWDER200", 12, 40, 50, 40, 0, 0)); // LOW STOCK: 10 <= 12 (ALERT)

            // 12. HEALTH & HYGIENE
            catalog.add(new ProductSeed("Dettol Antiseptic Liquid 550ml", "DETTOL550", 12, 50, 70, 42, 0, 0));
            catalog.add(new ProductSeed("Savlon Antiseptic Disinfectant Liquid 500ml", "SAVLON500", 10, 40, 55, 30, 0, 0));
            catalog.add(new ProductSeed("Whisper Choice Ultra Sanitary Pads XL 20 Pcs", "WHISPERXL20", 15, 60, 80, 50, 0, 0));
            catalog.add(new ProductSeed("Stayfree Secure Cottony Sanitary Pads XL 40 Pcs", "STAYFREEXL40", 15, 60, 75, 45, 0, 0));
            catalog.add(new ProductSeed("Carefree Breathable Panty Liners 20 Pcs", "CAREFREE20", 10, 40, 45, 25, 0, 0));

            // 13. CONFECTIONERY
            catalog.add(new ProductSeed("Cadbury Dairy Milk Silk Chocolate 150g", "DAIRYMILK150", 15, 60, 90, 55, 0, 1));
            catalog.add(new ProductSeed("Cadbury 5 Star Chocolate Bar 40g", "FIVESTAR40", 25, 100, 120, 70, 0, 0));
            catalog.add(new ProductSeed("Nestle KitKat 4-Finger Chocolate Bar 38g", "KITKAT38", 20, 80, 120, 110, 0, 0)); // LOW STOCK: 10 <= 20 (ALERT) & Fast moving
            catalog.add(new ProductSeed("Cadbury Perk Glucose Chocolate Bar 28g", "PERK28", 25, 100, 110, 60, 0, 0));
            catalog.add(new ProductSeed("Nestle Munch Crunchy Chocolate Bar 25g", "MUNCH25", 25, 100, 110, 65, 0, 0));
            catalog.add(new ProductSeed("Cadbury Gems Tube 30g", "GEMS30", 20, 80, 90, 50, 0, 0));
            catalog.add(new ProductSeed("Parle Melody Chocolaty Candy Pouch 391g", "MELODY391", 10, 40, 50, 30, 0, 0));

            // 14. INSTANT FOODS
            catalog.add(new ProductSeed("Maggi 2-Minute Masala Instant Noodles 420g", "MAGGI420", 25, 120, 200, 180, 0, 0)); // LOW STOCK: 20 <= 25 (ALERT) & TOP FAST MOVING (180 sold)
            catalog.add(new ProductSeed("Sunfeast Yippee Magic Masala Noodles 240g", "YIPPEE240", 20, 80, 100, 60, 0, 0));
            catalog.add(new ProductSeed("Bambino Macaroni Pasta 500g", "BAMBINOPASTA500", 12, 50, 65, 38, 0, 0));
            catalog.add(new ProductSeed("MTR Quick Instant Upma Breakfast Mix 500g", "MTRUPMA500", 10, 40, 50, 30, 0, 0));
            catalog.add(new ProductSeed("MTR Instant Rava Idli Breakfast Mix 500g", "MTRIDLI500", 10, 40, 50, 32, 0, 0));
            catalog.add(new ProductSeed("MTR Spiced Authentic Sambar Powder 200g", "MTRSAMBAR200", 10, 40, 55, 35, 0, 0));

            // 15. FROZEN FOODS
            catalog.add(new ProductSeed("McCain French Fries Crispy 420g", "MCCAINFRIES420", 10, 40, 50, 30, 0, 0));
            catalog.add(new ProductSeed("McCain Aloo Tikki Mazedaar Masala 400g", "MCCAINTIKKI400", 10, 40, 50, 32, 0, 0));
            catalog.add(new ProductSeed("Sumeru Green Peas Frozen 500g", "SUMERUPEAS500", 12, 50, 60, 35, 0, 0));
            catalog.add(new ProductSeed("Godrej Yummiez Chicken Nuggets 500g", "GODREJNUGGETS500", 8, 30, 45, 38, 0, 0)); // LOW STOCK: 7 <= 8 (ALERT)

            // Seed each product and its corresponding ledger movements
            int dayOffset = 1;
            for (ProductSeed item : catalog) {
                Product product = productRepository.save(new Product(item.name, item.sku, item.threshold, item.reorderQty));

                // 1. Initial Purchase
                if (item.purchase > 0) {
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.PURCHASE, item.purchase, LocalDate.of(2026, 9, 1)
                    ));
                }

                // 2. Sales (split across dates for rich movement history)
                if (item.sales > 0) {
                    int firstSale = Math.min(item.sales, item.sales / 2 + 5);
                    int remainingSale = item.sales - firstSale;

                    LocalDate date1 = LocalDate.of(2026, 9, Math.min(28, (dayOffset % 10) + 2));
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.SALE, firstSale, date1
                    ));

                    if (remainingSale > 0) {
                        LocalDate date2 = LocalDate.of(2026, 9, Math.min(28, (dayOffset % 15) + 12));
                        stockMovementService.recordMovement(new CreateStockMovementRequest(
                                product.getId(), MovementType.SALE, remainingSale, date2
                        ));
                    }
                }

                // 3. Returns (if any)
                if (item.returns > 0) {
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.RETURN, item.returns, LocalDate.of(2026, 9, 20)
                    ));
                }

                // 4. Damages (if any)
                if (item.damage > 0) {
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.DAMAGE, item.damage, LocalDate.of(2026, 9, 22)
                    ));
                }

                dayOffset++;
            }

            log.info("Successfully seeded {} products across all 15 retail categories with stock movement ledger history!", catalog.size());
        }
    }
}
