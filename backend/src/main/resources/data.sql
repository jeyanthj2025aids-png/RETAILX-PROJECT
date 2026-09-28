-- ============================================================================
-- RetailX Sample Products (94 Products across 15 Realistic Retail Categories)
-- ============================================================================

INSERT INTO products (name, sku, reorder_threshold, reorder_quantity) VALUES
-- 1. Beverages
('Coca-Cola 500ml', 'COKE500', 20, 100),
('Pepsi 500ml', 'PEPSI500', 20, 100),
('Sprite 500ml', 'SPRITE500', 15, 80),
('Fanta 500ml', 'FANTA500', 15, 80),
('Maaza Mango 600ml', 'MAAZA600', 15, 75),
('Thums Up 750ml', 'THUMSUP750', 20, 100),
('Real Fruit Juice 1L', 'REALJUICE1L', 12, 60),
('Bisleri Mineral Water 1L', 'BISLERI1L', 30, 150),

-- 2. Biscuits & Snacks
('Parle-G 800g', 'PARLEG800', 15, 80),
('Britannia Good Day 200g', 'GOODDAY200', 15, 60),
('Oreo Cookies 120g', 'OREO120', 12, 50),
('Britannia Bourbon 150g', 'BOURBON150', 10, 50),
('Sunfeast Dark Fantasy 150g', 'DARKFANTASY150', 10, 40),
('Bingo Mad Angles 90g', 'BINGO90', 15, 60),
('Lays Classic Salted 50g', 'LAYS50', 25, 100),
('Kurkure Masala Munch 90g', 'KURKURE90', 20, 90),
('Haldiram''s Bhujia 200g', 'HBHUJIA200', 12, 50),
('Too Yumm Veggie Stix 60g', 'TOOYUMM60', 10, 40),

-- 3. Rice & Grains
('India Gate Basmati Rice 5kg', 'INGATE5KG', 10, 40),
('Aashirvaad Superior Rice 5kg', 'AARRICE5', 12, 60),
('Fortune Special Basmati Rice 5kg', 'FORTBAS5KG', 8, 35),
('Sona Masoori Raw Rice 5kg', 'SONAMAS5KG', 15, 50),
('Tata Sampann Toor Dal 1kg', 'TOORDAL1KG', 10, 40),
('Tata Sampann Moong Dal 1kg', 'MOONGDAL1KG', 8, 35),
('Chana Dal 1kg', 'CHANADAL1KG', 8, 30),
('Urad Dal White 1kg', 'URADDAL1KG', 8, 30),
('Premium Red Rajma 1kg', 'RAJMA1KG', 6, 25),

-- 4. Cooking Essentials
('Fortune Sunflower Oil 1L', 'FORTOIL1L', 15, 60),
('Gold Winner Refined Oil 1L', 'GOLDWIN1L', 12, 50),
('Aashirvaad Shudh Chakki Atta 5kg', 'AARATTA5KG', 15, 60),
('Tata Salt Vacuum Evaporated 1kg', 'TATASALT1KG', 20, 100),
('Madhur Pure Refined Sugar 1kg', 'SUGAR1KG', 20, 80),
('Everest Turmeric Powder 200g', 'EVTURM200', 10, 40),
('Everest Chilli Powder 200g', 'EVCHILLI200', 10, 40),
('Catch Cumin Seeds 100g', 'JEERA100', 8, 30),
('Catch Mustard Seeds 100g', 'MUSTARD100', 8, 30),
('Red Label Tea Powder 500g', 'REDLABEL500', 12, 50),
('Nescafe Classic Instant Coffee 200g', 'NESCAFE200', 10, 40),

-- 5. Personal Care
('Dove Cream Beauty Bathing Bar', 'DOVE100', 12, 50),
('Lux Rose Soap 125g', 'LUX125', 10, 50),
('Lifebuoy Total Soap 125g', 'LIFEBUOY125', 15, 60),
('Pears Pure & Gentle Soap', 'PEARS125', 10, 40),
('Colgate Strong Teeth Toothpaste 200g', 'COLGATE200', 15, 60),
('Closeup Red Hot Toothpaste 150g', 'CLOSEUP150', 12, 50),
('Head & Shoulders Shampoo 200ml', 'HS200', 8, 40),
('Clinic Plus Strong & Long Shampoo 340ml', 'CLINIC340', 10, 50),
('Sunsilk Black Shine Shampoo 350ml', 'SUNSILK350', 10, 40),
('Parachute Pure Coconut Oil 500ml', 'PARACHUTE500', 12, 50),
('Nivea Nourishing Body Lotion 400ml', 'NIVEA400', 8, 30),
('Vaseline Original Jelly 100g', 'VASELINE100', 10, 40),

-- 6. Home Cleaning
('Surf Excel Easy Wash Detergent 2kg', 'SURFEXCEL2KG', 10, 40),
('Ariel Matic Top Load Detergent 2kg', 'ARIEL2KG', 8, 35),
('Rin Detergent Bar 250g', 'RINBAR250', 20, 80),
('Vim Dishwash Liquid Gel 500ml', 'VIMGEL500', 15, 60),
('Harpic Power Plus Toilet Cleaner 1L', 'HARPIC1L', 12, 50),
('Lizol Citrus Floor Cleaner 1L', 'LIZOL1L', 12, 50),
('Domex Disinfectant Floor Cleaner 1L', 'DOMEX1L', 10, 40),
('Colin Glass & Surface Cleaner 500ml', 'COLIN500', 8, 30),
('Comfort Fabric Conditioner 860ml', 'COMFORT860', 8, 30),
('Good Knight Activ+ Mosquito Refill 45ml', 'GOODKNIGHT45', 15, 60),

-- 7. Dairy & Breakfast
('Amul Taaza Homogenised Toned Milk 1L', 'AMULMILK1L', 20, 100),
('Amul Pasteurized Salted Butter 500g', 'AMULBUTTER500', 12, 50),
('Amul Processed Cheese Blocks 200g', 'AMULCHEESE200', 10, 40),
('Amul Masti Dahi Tub 400g', 'AMULCURD400', 15, 60),
('Kellogg''s Original Corn Flakes 500g', 'KELLOGGS500', 10, 40),
('Quaker Rolled White Oats 1kg', 'QUAKEROATS1KG', 10, 40),
('Horlicks Classic Malt Drink 500g', 'HORLICKS500', 8, 35),
('Complan Nutrition Drink Royale Chocolate 500g', 'COMPLAN500', 8, 35),
('Nestle Sweetened Milkmaid 380g', 'MILKMAID380', 8, 30),

-- 8. Packaged Foods
('Del Monte Tomato Ketchup 950g', 'DELKETCHUP950', 10, 40),
('Veeba Eggless Mayonnaise 250g', 'VEEBAMAYO250', 8, 30),
('Knorr Classic Mixed Veg Soup 43g', 'KNORRSOUP43', 15, 60),
('Hershey''s Chocolate Syrup 623g', 'HERSHEY623', 6, 25),
('Nutella Hazelnut Spread 350g', 'NUTELLA350', 8, 30),

-- 9. Household Items
('Scotch-Brite Sponge Scrub Pad', 'SCOTCHBRITE3P', 20, 80),
('Shalimar Garbage Bags 30 Pcs Medium', 'GARBAGEBAG30', 15, 50),
('Hindalco Freshwrap Aluminium Foil 18m', 'ALFOIL18M', 10, 40),
('Freshwrapp Cling Film 30m', 'CLINGFILM30M', 8, 30),
('Origami Kitchen Paper Towels 2 Rolls', 'PAPERTOWEL2R', 12, 40),
('Paseo Soft Facial Tissues 200 Pulls', 'TISSUEBOX200', 15, 50),
('Duracell Ultra Alkaline Batteries AA 4-Pack', 'DURACELLAA4', 12, 40),
('Duracell Ultra Alkaline Batteries AAA 4-Pack', 'DURACELLAAA4', 12, 40),

-- 10. Stationery
('Classmate A4 Ruled Spiral Notebook 300p', 'CLASSNOTE300', 20, 80),
('Reynolds 045 Fine Ball Pen Blue Pack 5', 'REYNOLDSBL5', 25, 100),
('Reynolds 045 Fine Ball Pen Black Pack 5', 'REYNOLDSBK5', 20, 80),
('Apsara Platinum Extra Dark Pencils Pack 10', 'APSARAPEN10', 15, 60),
('Camlin Exam Eraser Big Pack 5', 'CAMLINERASER5', 20, 80),
('Faber-Castell Triangular Sharpener Pack 5', 'SHARPENER5', 15, 60),
('Camlin Permanent Marker Black', 'MARKERBK', 12, 50),
('Faber-Castell Textliner Highlighter Pack 5', 'HIGHLIGHT5', 10, 40),
('Fevistik Super Glue Stick 15g', 'FEVISTIK15', 15, 60),
('JK Copier A4 Copier Paper 75GSM 500 Sheets', 'JKCOPIER500', 10, 40),

-- 11. Baby Care
('Pampers All Round Protection Diapers M 44 Pcs', 'PAMPERSM44', 10, 40),
('Huggies Wonder Pants Diaper Large 34 Pcs', 'HUGGIESL34', 10, 40),
('Johnson''s Baby Nourishing Soap 75g', 'JHBABYSOAP75', 15, 60),
('Johnson''s No More Tears Baby Shampoo 200ml', 'JHBABYSHAMP200', 10, 40),
('Himalaya Gentle Baby Wipes 72 Pcs', 'HIMBABYWIPES72', 15, 50),
('Johnson''s Baby Powder 200g', 'JHBABYPOWDER200', 12, 40),

-- 12. Health & Hygiene
('Dettol Antiseptic Liquid 550ml', 'DETTOL550', 12, 50),
('Savlon Antiseptic Disinfectant Liquid 500ml', 'SAVLON500', 10, 40),
('Whisper Choice Ultra Sanitary Pads XL 20 Pcs', 'WHISPERXL20', 15, 60),
('Stayfree Secure Cottony Sanitary Pads XL 40 Pcs', 'STAYFREEXL40', 15, 60),
('Carefree Breathable Panty Liners 20 Pcs', 'CAREFREE20', 10, 40),

-- 13. Confectionery
('Cadbury Dairy Milk Silk Chocolate 150g', 'DAIRYMILK150', 15, 60),
('Cadbury 5 Star Chocolate Bar 40g', 'FIVESTAR40', 25, 100),
('Nestle KitKat 4-Finger Chocolate Bar 38g', 'KITKAT38', 20, 80),
('Cadbury Perk Glucose Chocolate Bar 28g', 'PERK28', 25, 100),
('Nestle Munch Crunchy Chocolate Bar 25g', 'MUNCH25', 25, 100),
('Cadbury Gems Tube 30g', 'GEMS30', 20, 80),
('Parle Melody Chocolaty Candy Pouch 391g', 'MELODY391', 10, 40),

-- 14. Instant Foods
('Maggi 2-Minute Masala Instant Noodles 420g', 'MAGGI420', 25, 120),
('Sunfeast Yippee Magic Masala Noodles 240g', 'YIPPEE240', 20, 80),
('Bambino Macaroni Pasta 500g', 'BAMBINOPASTA500', 12, 50),
('MTR Quick Instant Upma Breakfast Mix 500g', 'MTRUPMA500', 10, 40),
('MTR Instant Rava Idli Breakfast Mix 500g', 'MTRIDLI500', 10, 40),
('MTR Spiced Authentic Sambar Powder 200g', 'MTRSAMBAR200', 10, 40),

-- 15. Frozen Foods
('McCain French Fries Crispy 420g', 'MCCAINFRIES420', 10, 40),
('McCain Aloo Tikki Mazedaar Masala 400g', 'MCCAINTIKKI400', 10, 40),
('Sumeru Green Peas Frozen 500g', 'SUMERUPEAS500', 12, 50),
('Godrej Yummiez Chicken Nuggets 500g', 'GODREJNUGGETS500', 8, 30);
