-- Add organizer_id column to event table and FK constraint
ALTER TABLE event ADD COLUMN IF NOT EXISTS organizer_id BIGINT;
ALTER TABLE event ADD CONSTRAINT IF NOT EXISTS fk_event_organizer FOREIGN KEY (organizer_id) REFERENCES event_organizer(id);
