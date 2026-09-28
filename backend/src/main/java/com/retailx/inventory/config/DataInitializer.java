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
            log.info("Seeding realistic retail catalog (94 products) with ALL 4 movement types: PURCHASE, SALE, RETURN, DAMAGE...");

            List<ProductSeed> catalog = new ArrayList<>();

            // 1. BEVERAGES (Stock: Purchase + Return - Sale - Damage)
            catalog.add(new ProductSeed("Coca-Cola 500ml", "COKE500", 20, 100, 180, 140, 5, 6)); // Stock = 185 - 146 = 39
            catalog.add(new ProductSeed("Pepsi 500ml", "PEPSI500", 20, 100, 120, 75, 4, 3));    // Stock = 124 - 78 = 46
            catalog.add(new ProductSeed("Sprite 500ml", "SPRITE500", 15, 80, 100, 60, 3, 2));    // Stock = 103 - 62 = 41
            catalog.add(new ProductSeed("Fanta 500ml", "FANTA500", 15, 80, 90, 50, 4, 2));       // Stock = 94 - 52 = 42
            catalog.add(new ProductSeed("Maaza Mango 600ml", "MAAZA600", 15, 75, 110, 70, 3, 4)); // Stock = 113 - 74 = 39
            catalog.add(new ProductSeed("Thums Up 750ml", "THUMSUP750", 20, 100, 130, 95, 2, 5)); // Stock = 132 - 100 = 32
            catalog.add(new ProductSeed("Real Fruit Juice 1L", "REALJUICE1L", 12, 60, 70, 62, 2, 2)); // LOW STOCK: (72 - 64 = 8 <= 12) (ALERT)
            catalog.add(new ProductSeed("Bisleri Mineral Water 1L", "BISLERI1L", 30, 150, 200, 130, 8, 3)); // Stock = 208 - 133 = 75

            // 2. BISCUITS & SNACKS
            catalog.add(new ProductSeed("Parle-G 800g", "PARLEG800", 15, 80, 100, 85, 3, 3)); // LOW STOCK: (103 - 88 = 15 <= 15) (ALERT)
            catalog.add(new ProductSeed("Britannia Good Day 200g", "GOODDAY200", 15, 60, 90, 55, 3, 2)); // Stock = 93 - 57 = 36
            catalog.add(new ProductSeed("Oreo Cookies 120g", "OREO120", 12, 50, 80, 48, 4, 3)); // Stock = 84 - 51 = 33
            catalog.add(new ProductSeed("Britannia Bourbon 150g", "BOURBON150", 10, 50, 70, 40, 2, 2)); // Stock = 72 - 42 = 30
            catalog.add(new ProductSeed("Sunfeast Dark Fantasy 150g", "DARKFANTASY150", 10, 40, 60, 35, 3, 2)); // Stock = 63 - 37 = 26
            catalog.add(new ProductSeed("Bingo Mad Angles 90g", "BINGO90", 15, 60, 85, 50, 4, 3)); // Stock = 89 - 53 = 36
            catalog.add(new ProductSeed("Lays Classic Salted 50g", "LAYS50", 25, 100, 150, 75, 5, 4)); // Stock = 155 - 79 = 76
            catalog.add(new ProductSeed("Kurkure Masala Munch 90g", "KURKURE90", 20, 90, 110, 65, 3, 3)); // Stock = 113 - 68 = 45
            catalog.add(new ProductSeed("Haldiram's Bhujia 200g", "HBHUJIA200", 12, 50, 75, 45, 2, 2)); // Stock = 77 - 47 = 30
            catalog.add(new ProductSeed("Too Yumm Veggie Stix 60g", "TOOYUMM60", 10, 40, 50, 42, 2, 2)); // LOW STOCK: (52 - 44 = 8 <= 10) (ALERT)

            // 3. RICE & GRAINS
            catalog.add(new ProductSeed("India Gate Basmati Rice 5kg", "INGATE5KG", 10, 40, 60, 35, 3, 2)); // Stock = 63 - 37 = 26
            catalog.add(new ProductSeed("Aashirvaad Superior Rice 5kg", "AARRICE5", 12, 60, 120, 115, 2, 2)); // LOW STOCK: (122 - 117 = 5 <= 12) (ALERT)
            catalog.add(new ProductSeed("Fortune Special Basmati Rice 5kg", "FORTBAS5KG", 8, 35, 50, 30, 2, 1)); // Stock = 52 - 31 = 21
            catalog.add(new ProductSeed("Sona Masoori Raw Rice 5kg", "SONAMAS5KG", 15, 50, 80, 50, 4, 2)); // Stock = 84 - 52 = 32
            catalog.add(new ProductSeed("Tata Sampann Toor Dal 1kg", "TOORDAL1KG", 10, 40, 70, 45, 3, 2)); // Stock = 73 - 47 = 26
            catalog.add(new ProductSeed("Tata Sampann Moong Dal 1kg", "MOONGDAL1KG", 8, 35, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("Chana Dal 1kg", "CHANADAL1KG", 8, 30, 45, 38, 2, 2)); // LOW STOCK: (47 - 40 = 7 <= 8) (ALERT)
            catalog.add(new ProductSeed("Urad Dal White 1kg", "URADDAL1KG", 8, 30, 40, 25, 2, 1)); // Stock = 42 - 26 = 16
            catalog.add(new ProductSeed("Premium Red Rajma 1kg", "RAJMA1KG", 6, 25, 35, 20, 2, 1)); // Stock = 37 - 21 = 16

            // 4. COOKING ESSENTIALS
            catalog.add(new ProductSeed("Fortune Sunflower Oil 1L", "FORTOIL1L", 15, 60, 100, 92, 3, 3)); // LOW STOCK: (103 - 95 = 8 <= 15) (ALERT)
            catalog.add(new ProductSeed("Gold Winner Refined Oil 1L", "GOLDWIN1L", 12, 50, 80, 50, 3, 2)); // Stock = 83 - 52 = 31
            catalog.add(new ProductSeed("Aashirvaad Shudh Chakki Atta 5kg", "AARATTA5KG", 15, 60, 110, 65, 4, 3)); // Stock = 114 - 68 = 46
            catalog.add(new ProductSeed("Tata Salt Vacuum Evaporated 1kg", "TATASALT1KG", 20, 100, 150, 90, 5, 3)); // Stock = 155 - 93 = 62
            catalog.add(new ProductSeed("Madhur Pure Refined Sugar 1kg", "SUGAR1KG", 20, 80, 120, 70, 4, 2)); // Stock = 124 - 72 = 52
            catalog.add(new ProductSeed("Everest Turmeric Powder 200g", "EVTURM200", 10, 40, 60, 35, 2, 2)); // Stock = 62 - 37 = 25
            catalog.add(new ProductSeed("Everest Chilli Powder 200g", "EVCHILLI200", 10, 40, 60, 38, 3, 2)); // Stock = 63 - 40 = 23
            catalog.add(new ProductSeed("Catch Cumin Seeds 100g", "JEERA100", 8, 30, 40, 22, 2, 1)); // Stock = 42 - 23 = 19
            catalog.add(new ProductSeed("Catch Mustard Seeds 100g", "MUSTARD100", 8, 30, 40, 20, 2, 1)); // Stock = 42 - 21 = 21
            catalog.add(new ProductSeed("Red Label Tea Powder 500g", "REDLABEL500", 12, 50, 80, 52, 3, 2)); // Stock = 83 - 54 = 29
            catalog.add(new ProductSeed("Nescafe Classic Instant Coffee 200g", "NESCAFE200", 10, 40, 50, 42, 2, 2)); // LOW STOCK: (52 - 44 = 8 <= 10) (ALERT)

            // 5. PERSONAL CARE
            catalog.add(new ProductSeed("Dove Cream Beauty Bathing Bar", "DOVE100", 12, 50, 80, 45, 3, 2)); // Stock = 83 - 47 = 36
            catalog.add(new ProductSeed("Lux Rose Soap 125g", "LUX125", 10, 50, 80, 20, 2, 2)); // Stock = 82 - 22 = 60
            catalog.add(new ProductSeed("Lifebuoy Total Soap 125g", "LIFEBUOY125", 15, 60, 90, 55, 3, 2)); // Stock = 93 - 57 = 36
            catalog.add(new ProductSeed("Pears Pure & Gentle Soap", "PEARS125", 10, 40, 60, 35, 2, 2)); // Stock = 62 - 37 = 25
            catalog.add(new ProductSeed("Colgate Strong Teeth Toothpaste 200g", "COLGATE200", 15, 60, 80, 68, 3, 3)); // LOW STOCK: (83 - 71 = 12 <= 15) (ALERT)
            catalog.add(new ProductSeed("Closeup Red Hot Toothpaste 150g", "CLOSEUP150", 12, 50, 70, 40, 2, 2)); // Stock = 72 - 42 = 30
            catalog.add(new ProductSeed("Head & Shoulders Shampoo 200ml", "HS200", 8, 40, 50, 33, 2, 2)); // Stock = 52 - 35 = 17
            catalog.add(new ProductSeed("Clinic Plus Strong & Long Shampoo 340ml", "CLINIC340", 10, 50, 65, 35, 2, 2)); // Stock = 67 - 37 = 30
            catalog.add(new ProductSeed("Sunsilk Black Shine Shampoo 350ml", "SUNSILK350", 10, 40, 55, 30, 2, 2)); // Stock = 57 - 32 = 25
            catalog.add(new ProductSeed("Parachute Pure Coconut Oil 500ml", "PARACHUTE500", 12, 50, 75, 45, 3, 2)); // Stock = 78 - 47 = 31
            catalog.add(new ProductSeed("Nivea Nourishing Body Lotion 400ml", "NIVEA400", 8, 30, 45, 25, 2, 1)); // Stock = 47 - 26 = 21
            catalog.add(new ProductSeed("Vaseline Original Jelly 100g", "VASELINE100", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20

            // 6. HOME CLEANING
            catalog.add(new ProductSeed("Surf Excel Easy Wash Detergent 2kg", "SURFEXCEL2KG", 10, 40, 60, 52, 2, 2)); // LOW STOCK: (62 - 54 = 8 <= 10) (ALERT)
            catalog.add(new ProductSeed("Ariel Matic Top Load Detergent 2kg", "ARIEL2KG", 8, 35, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("Rin Detergent Bar 250g", "RINBAR250", 20, 80, 100, 60, 4, 3)); // Stock = 104 - 63 = 41
            catalog.add(new ProductSeed("Vim Dishwash Liquid Gel 500ml", "VIMGEL500", 15, 60, 90, 55, 3, 2)); // Stock = 93 - 57 = 36
            catalog.add(new ProductSeed("Harpic Power Plus Toilet Cleaner 1L", "HARPIC1L", 12, 50, 80, 48, 3, 2)); // Stock = 83 - 50 = 33
            catalog.add(new ProductSeed("Lizol Citrus Floor Cleaner 1L", "LIZOL1L", 12, 50, 75, 45, 3, 2)); // Stock = 78 - 47 = 31
            catalog.add(new ProductSeed("Domex Disinfectant Floor Cleaner 1L", "DOMEX1L", 10, 40, 55, 32, 2, 2)); // Stock = 57 - 34 = 23
            catalog.add(new ProductSeed("Colin Glass & Surface Cleaner 500ml", "COLIN500", 8, 30, 40, 22, 2, 1)); // Stock = 42 - 23 = 19
            catalog.add(new ProductSeed("Comfort Fabric Conditioner 860ml", "COMFORT860", 8, 30, 45, 28, 2, 1)); // Stock = 47 - 29 = 18
            catalog.add(new ProductSeed("Good Knight Activ+ Mosquito Refill 45ml", "GOODKNIGHT45", 15, 60, 90, 55, 4, 3)); // Stock = 94 - 58 = 36

            // 7. DAIRY & BREAKFAST
            catalog.add(new ProductSeed("Amul Taaza Homogenised Toned Milk 1L", "AMULMILK1L", 20, 100, 150, 135, 4, 4)); // LOW STOCK: (154 - 139 = 15 <= 20) (ALERT)
            catalog.add(new ProductSeed("Amul Pasteurized Salted Butter 500g", "AMULBUTTER500", 12, 50, 80, 60, 3, 2)); // Stock = 83 - 62 = 21
            catalog.add(new ProductSeed("Amul Processed Cheese Blocks 200g", "AMULCHEESE200", 10, 40, 60, 35, 2, 2)); // Stock = 62 - 37 = 25
            catalog.add(new ProductSeed("Amul Masti Dahi Tub 400g", "AMULCURD400", 15, 60, 80, 50, 3, 2)); // Stock = 83 - 52 = 31
            catalog.add(new ProductSeed("Kellogg's Original Corn Flakes 500g", "KELLOGGS500", 10, 40, 55, 30, 2, 2)); // Stock = 57 - 32 = 25
            catalog.add(new ProductSeed("Quaker Rolled White Oats 1kg", "QUAKEROATS1KG", 10, 40, 60, 35, 3, 2)); // Stock = 63 - 37 = 26
            catalog.add(new ProductSeed("Horlicks Classic Malt Drink 500g", "HORLICKS500", 8, 35, 45, 25, 2, 1)); // Stock = 47 - 26 = 21
            catalog.add(new ProductSeed("Complan Nutrition Drink Royale Chocolate 500g", "COMPLAN500", 8, 35, 45, 28, 2, 1)); // Stock = 47 - 29 = 18
            catalog.add(new ProductSeed("Nestle Sweetened Milkmaid 380g", "MILKMAID380", 8, 30, 40, 24, 2, 1)); // Stock = 42 - 25 = 17

            // 8. PACKAGED FOODS
            catalog.add(new ProductSeed("Del Monte Tomato Ketchup 950g", "DELKETCHUP950", 10, 40, 60, 38, 3, 2)); // Stock = 63 - 40 = 23
            catalog.add(new ProductSeed("Veeba Eggless Mayonnaise 250g", "VEEBAMAYO250", 8, 30, 45, 26, 2, 1)); // Stock = 47 - 27 = 20
            catalog.add(new ProductSeed("Knorr Classic Mixed Veg Soup 43g", "KNORRSOUP43", 15, 60, 70, 40, 3, 2)); // Stock = 73 - 42 = 31
            catalog.add(new ProductSeed("Hershey's Chocolate Syrup 623g", "HERSHEY623", 6, 25, 35, 20, 2, 1)); // Stock = 37 - 21 = 16
            catalog.add(new ProductSeed("Nutella Hazelnut Spread 350g", "NUTELLA350", 8, 30, 40, 25, 2, 1)); // Stock = 42 - 26 = 16

            // 9. HOUSEHOLD ITEMS
            catalog.add(new ProductSeed("Scotch-Brite Sponge Scrub Pad", "SCOTCHBRITE3P", 20, 80, 100, 60, 4, 3)); // Stock = 104 - 63 = 41
            catalog.add(new ProductSeed("Shalimar Garbage Bags 30 Pcs Medium", "GARBAGEBAG30", 15, 50, 70, 40, 3, 2)); // Stock = 73 - 42 = 31
            catalog.add(new ProductSeed("Hindalco Freshwrap Aluminium Foil 18m", "ALFOIL18M", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("Freshwrapp Cling Film 30m", "CLINGFILM30M", 8, 30, 40, 24, 2, 1)); // Stock = 42 - 25 = 17
            catalog.add(new ProductSeed("Origami Kitchen Paper Towels 2 Rolls", "PAPERTOWEL2R", 12, 40, 55, 32, 2, 2)); // Stock = 57 - 34 = 23
            catalog.add(new ProductSeed("Paseo Soft Facial Tissues 200 Pulls", "TISSUEBOX200", 15, 50, 65, 38, 3, 2)); // Stock = 68 - 40 = 28
            catalog.add(new ProductSeed("Duracell Ultra Alkaline Batteries AA 4-Pack", "DURACELLAA4", 12, 40, 60, 35, 3, 2)); // Stock = 63 - 37 = 26
            catalog.add(new ProductSeed("Duracell Ultra Alkaline Batteries AAA 4-Pack", "DURACELLAAA4", 12, 40, 55, 48, 2, 2)); // LOW STOCK: (57 - 50 = 7 <= 12) (ALERT)

            // 10. STATIONERY
            catalog.add(new ProductSeed("Classmate A4 Ruled Spiral Notebook 300p", "CLASSNOTE300", 20, 80, 100, 60, 5, 2)); // Stock = 105 - 62 = 43
            catalog.add(new ProductSeed("Reynolds 045 Fine Ball Pen Blue Pack 5", "REYNOLDSBL5", 25, 100, 150, 80, 5, 3)); // Stock = 155 - 83 = 72
            catalog.add(new ProductSeed("Reynolds 045 Fine Ball Pen Black Pack 5", "REYNOLDSBK5", 20, 80, 100, 55, 4, 3)); // Stock = 104 - 58 = 46
            catalog.add(new ProductSeed("Apsara Platinum Extra Dark Pencils Pack 10", "APSARAPEN10", 15, 60, 80, 45, 3, 2)); // Stock = 83 - 47 = 36
            catalog.add(new ProductSeed("Camlin Exam Eraser Big Pack 5", "CAMLINERASER5", 20, 80, 90, 50, 4, 2)); // Stock = 94 - 52 = 42
            catalog.add(new ProductSeed("Faber-Castell Triangular Sharpener Pack 5", "SHARPENER5", 15, 60, 70, 40, 3, 2)); // Stock = 73 - 42 = 31
            catalog.add(new ProductSeed("Camlin Permanent Marker Black", "MARKERBK", 12, 50, 60, 35, 2, 2)); // Stock = 62 - 37 = 25
            catalog.add(new ProductSeed("Faber-Castell Textliner Highlighter Pack 5", "HIGHLIGHT5", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("Fevistik Super Glue Stick 15g", "FEVISTIK15", 15, 60, 75, 42, 3, 2)); // Stock = 78 - 44 = 34
            catalog.add(new ProductSeed("JK Copier A4 Copier Paper 75GSM 500 Sheets", "JKCOPIER500", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20

            // 11. BABY CARE
            catalog.add(new ProductSeed("Pampers All Round Protection Diapers M 44 Pcs", "PAMPERSM44", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("Huggies Wonder Pants Diaper Large 34 Pcs", "HUGGIESL34", 10, 40, 50, 32, 2, 2)); // Stock = 52 - 34 = 18
            catalog.add(new ProductSeed("Johnson's Baby Nourishing Soap 75g", "JHBABYSOAP75", 15, 60, 70, 40, 3, 2)); // Stock = 73 - 42 = 31
            catalog.add(new ProductSeed("Johnson's No More Tears Baby Shampoo 200ml", "JHBABYSHAMP200", 10, 40, 50, 28, 2, 2)); // Stock = 52 - 30 = 22
            catalog.add(new ProductSeed("Himalaya Gentle Baby Wipes 72 Pcs", "HIMBABYWIPES72", 15, 50, 65, 38, 3, 2)); // Stock = 68 - 40 = 28
            catalog.add(new ProductSeed("Johnson's Baby Powder 200g", "JHBABYPOWDER200", 12, 40, 50, 40, 2, 2)); // LOW STOCK: (52 - 42 = 10 <= 12) (ALERT)

            // 12. HEALTH & HYGIENE
            catalog.add(new ProductSeed("Dettol Antiseptic Liquid 550ml", "DETTOL550", 12, 50, 70, 42, 3, 2)); // Stock = 73 - 44 = 29
            catalog.add(new ProductSeed("Savlon Antiseptic Disinfectant Liquid 500ml", "SAVLON500", 10, 40, 55, 30, 2, 2)); // Stock = 57 - 32 = 25
            catalog.add(new ProductSeed("Whisper Choice Ultra Sanitary Pads XL 20 Pcs", "WHISPERXL20", 15, 60, 80, 50, 3, 2)); // Stock = 83 - 52 = 31
            catalog.add(new ProductSeed("Stayfree Secure Cottony Sanitary Pads XL 40 Pcs", "STAYFREEXL40", 15, 60, 75, 45, 3, 2)); // Stock = 78 - 47 = 31
            catalog.add(new ProductSeed("Carefree Breathable Panty Liners 20 Pcs", "CAREFREE20", 10, 40, 45, 25, 2, 2)); // Stock = 47 - 27 = 20

            // 13. CONFECTIONERY
            catalog.add(new ProductSeed("Cadbury Dairy Milk Silk Chocolate 150g", "DAIRYMILK150", 15, 60, 90, 55, 4, 3)); // Stock = 94 - 58 = 36
            catalog.add(new ProductSeed("Cadbury 5 Star Chocolate Bar 40g", "FIVESTAR40", 25, 100, 120, 70, 5, 3)); // Stock = 125 - 73 = 52
            catalog.add(new ProductSeed("Nestle KitKat 4-Finger Chocolate Bar 38g", "KITKAT38", 20, 80, 120, 110, 2, 2)); // LOW STOCK: (122 - 112 = 10 <= 20) (ALERT)
            catalog.add(new ProductSeed("Cadbury Perk Glucose Chocolate Bar 28g", "PERK28", 25, 100, 110, 60, 4, 3)); // Stock = 114 - 63 = 51
            catalog.add(new ProductSeed("Nestle Munch Crunchy Chocolate Bar 25g", "MUNCH25", 25, 100, 110, 65, 4, 3)); // Stock = 114 - 68 = 46
            catalog.add(new ProductSeed("Cadbury Gems Tube 30g", "GEMS30", 20, 80, 90, 50, 3, 2)); // Stock = 93 - 52 = 41
            catalog.add(new ProductSeed("Parle Melody Chocolaty Candy Pouch 391g", "MELODY391", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20

            // 14. INSTANT FOODS
            catalog.add(new ProductSeed("Maggi 2-Minute Masala Instant Noodles 420g", "MAGGI420", 25, 120, 200, 180, 5, 5)); // LOW STOCK: (205 - 185 = 20 <= 25) (ALERT)
            catalog.add(new ProductSeed("Sunfeast Yippee Magic Masala Noodles 240g", "YIPPEE240", 20, 80, 100, 60, 4, 3)); // Stock = 104 - 63 = 41
            catalog.add(new ProductSeed("Bambino Macaroni Pasta 500g", "BAMBINOPASTA500", 12, 50, 65, 38, 3, 2)); // Stock = 68 - 40 = 28
            catalog.add(new ProductSeed("MTR Quick Instant Upma Breakfast Mix 500g", "MTRUPMA500", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("MTR Instant Rava Idli Breakfast Mix 500g", "MTRIDLI500", 10, 40, 50, 32, 2, 2)); // Stock = 52 - 34 = 18
            catalog.add(new ProductSeed("MTR Spiced Authentic Sambar Powder 200g", "MTRSAMBAR200", 10, 40, 55, 35, 2, 2)); // Stock = 57 - 37 = 20

            // 15. FROZEN FOODS
            catalog.add(new ProductSeed("McCain French Fries Crispy 420g", "MCCAINFRIES420", 10, 40, 50, 30, 2, 2)); // Stock = 52 - 32 = 20
            catalog.add(new ProductSeed("McCain Aloo Tikki Mazedaar Masala 400g", "MCCAINTIKKI400", 10, 40, 50, 32, 2, 2)); // Stock = 52 - 34 = 18
            catalog.add(new ProductSeed("Sumeru Green Peas Frozen 500g", "SUMERUPEAS500", 12, 50, 60, 35, 3, 2)); // Stock = 63 - 37 = 26
            catalog.add(new ProductSeed("Godrej Yummiez Chicken Nuggets 500g", "GODREJNUGGETS500", 8, 30, 45, 38, 2, 2)); // LOW STOCK: (47 - 40 = 7 <= 8) (ALERT)

            // Seed each product and interleave its PURCHASE, SALE, RETURN, DAMAGE movements across dates
            int dayOffset = 1;
            for (ProductSeed item : catalog) {
                Product product = productRepository.save(new Product(item.name, item.sku, item.threshold, item.reorderQty));

                // 1. Initial Purchase (Inflow)
                if (item.purchase > 0) {
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.PURCHASE, item.purchase, LocalDate.of(2026, 9, 1)
                    ));
                }

                // 2. Sales (Outflow)
                if (item.sales > 0) {
                    int firstSale = Math.min(item.sales, item.sales / 2 + 5);
                    int remainingSale = item.sales - firstSale;

                    LocalDate date1 = LocalDate.of(2026, 9, Math.min(28, (dayOffset % 10) + 2));
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.SALE, firstSale, date1
                    ));

                    if (remainingSale > 0) {
                        LocalDate date2 = LocalDate.of(2026, 9, Math.min(28, (dayOffset % 12) + 10));
                        stockMovementService.recordMovement(new CreateStockMovementRequest(
                                product.getId(), MovementType.SALE, remainingSale, date2
                        ));
                    }
                }

                // 3. Customer Returns (Inflow)
                if (item.returns > 0) {
                    LocalDate dateReturn = LocalDate.of(2026, 9, Math.min(28, (dayOffset % 10) + 18));
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.RETURN, item.returns, dateReturn
                    ));
                }

                // 4. Warehouse Damage (Outflow)
                if (item.damage > 0) {
                    LocalDate dateDamage = LocalDate.of(2026, 9, Math.min(28, (dayOffset % 8) + 20));
                    stockMovementService.recordMovement(new CreateStockMovementRequest(
                            product.getId(), MovementType.DAMAGE, item.damage, dateDamage
                    ));
                }

                dayOffset++;
            }

            log.info("Successfully seeded {} products across all 15 retail categories with all 4 movement types (PURCHASE, SALE, RETURN, DAMAGE)!", catalog.size());
        }
    }
}
