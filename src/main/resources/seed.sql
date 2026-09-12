-- YogaMart seed data
-- NOTE: seed users (admin/seller/buyer) are created programmatically at
-- application startup by AppContextListener, because their passwords must
-- be bcrypt-hashed at runtime rather than hardcoded here. This file only
-- seeds product catalog data, which references seller_id = 2 (the seeded
-- SELLER account created by the listener).
--
-- image_url values are real product photos hosted on Wikimedia Commons
-- (public domain / Creative Commons licensed), via Commons' stable
-- Special:FilePath redirect. Used for demo purposes.

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) VALUES
(2, 'Digital Thermometer', 'Fast 10-second oral/underarm digital thermometer with fever alert.', 249.00, 50, 'Health Monitoring', 'https://commons.wikimedia.org/wiki/Special:FilePath/Digital%20thermometer.jpg?width=500'),
(2, 'First Aid Kit - Compact', '32-piece compact first aid kit for home and travel use.', 399.00, 30, 'First Aid', 'https://commons.wikimedia.org/wiki/Special:FilePath/First%20Aid%20Kit.png?width=500'),
(2, 'Multivitamin Tablets (60ct)', 'Daily multivitamin and mineral supplement, 60 tablets.', 549.00, 100, 'Supplements', 'https://commons.wikimedia.org/wiki/Special:FilePath/B%20vitamin%20supplement%20tablets.jpg?width=500'),
(2, 'Blood Pressure Monitor', 'Automatic upper-arm digital blood pressure monitor.', 1499.00, 20, 'Health Monitoring', 'https://commons.wikimedia.org/wiki/Special:FilePath/Blood%20pressure%20measurement.jpg?width=500'),
(2, 'N95 Face Masks (Pack of 10)', 'NIOSH-rated N95 respirator masks, pack of 10.', 299.00, 200, 'Personal Protection', 'https://commons.wikimedia.org/wiki/Special:FilePath/N95%20respirator%20transparent.png?width=500'),
(2, 'Hand Sanitizer 500ml', '70% alcohol-based hand sanitizer, pump bottle.', 149.00, 150, 'Personal Protection', 'https://commons.wikimedia.org/wiki/Special:FilePath/Purell%20hand%20sanitizer%20gel%20in%20bottle%20%288487014501%29.jpg?width=500'),
(2, 'Omega-3 Fish Oil Capsules', 'Daily omega-3 fish oil softgels for heart and joint health.', 699.00, 80, 'Supplements', 'https://commons.wikimedia.org/wiki/Special:FilePath/Omega%203%20capsules%20in%20white%20bottle%20%2852715127894%29.jpg?width=500'),
(2, 'Surgical Face Masks (Pack of 50)', '3-ply disposable surgical face masks, box of 50.', 399.00, 120, 'Personal Protection', 'https://commons.wikimedia.org/wiki/Special:FilePath/3M%20Surgical%20N95%20Respirator.png?width=500');
