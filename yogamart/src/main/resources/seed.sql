-- YogaMart seed data
-- NOTE: seed users (admin/seller/buyer) are created programmatically at
-- application startup by AppContextListener, because their passwords must
-- be bcrypt-hashed at runtime rather than hardcoded here. This file only
-- seeds product catalog data, which references seller_id = 2 (the seeded
-- SELLER account created by the listener).

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) VALUES
(2, 'Digital Thermometer', 'Fast 10-second oral/underarm digital thermometer with fever alert.', 249.00, 50, 'Health Monitoring', 'https://via.placeholder.com/300x300?text=Thermometer'),
(2, 'First Aid Kit - Compact', '32-piece compact first aid kit for home and travel use.', 399.00, 30, 'First Aid', 'https://via.placeholder.com/300x300?text=First+Aid+Kit'),
(2, 'Multivitamin Tablets (60ct)', 'Daily multivitamin and mineral supplement, 60 tablets.', 549.00, 100, 'Supplements', 'https://via.placeholder.com/300x300?text=Multivitamin'),
(2, 'Blood Pressure Monitor', 'Automatic upper-arm digital blood pressure monitor.', 1499.00, 20, 'Health Monitoring', 'https://via.placeholder.com/300x300?text=BP+Monitor'),
(2, 'N95 Face Masks (Pack of 10)', 'NIOSH-rated N95 respirator masks, pack of 10.', 299.00, 200, 'Personal Protection', 'https://via.placeholder.com/300x300?text=N95+Masks'),
(2, 'Hand Sanitizer 500ml', '70% alcohol-based hand sanitizer, pump bottle.', 149.00, 150, 'Personal Protection', 'https://via.placeholder.com/300x300?text=Sanitizer');
