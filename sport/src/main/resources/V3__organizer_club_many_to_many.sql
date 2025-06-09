-- Migration for organizer-club many-to-many relationship
CREATE TABLE IF NOT EXISTS organizer_club (
    organizer_id BIGINT NOT NULL,
    club_id BIGINT NOT NULL,
    PRIMARY KEY (organizer_id, club_id),
    CONSTRAINT fk_organizer FOREIGN KEY (organizer_id) REFERENCES event_organizer(id) ON DELETE CASCADE,
    CONSTRAINT fk_club FOREIGN KEY (club_id) REFERENCES club(id) ON DELETE CASCADE
);

-- Remove old club_id column from event_organizer if it exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='event_organizer' AND column_name='club_id') THEN
        ALTER TABLE event_organizer DROP COLUMN club_id;
    END IF;
END $$;
