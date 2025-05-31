-- Add match_order column to match table
ALTER TABLE match ADD COLUMN match_order INT DEFAULT 0 COMMENT 'Order in which this match should be played';
