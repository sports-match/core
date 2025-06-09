-- Step 1: Add new columns if not already present
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='question' AND column_name='sport_id') THEN
        ALTER TABLE question ADD COLUMN sport_id BIGINT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='question' AND column_name='format') THEN
        ALTER TABLE question ADD COLUMN format VARCHAR(16) NOT NULL DEFAULT 'DOUBLES';
    END IF;
END $$;

-- Step 2: Add FK constraint if not present
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name='question' AND constraint_name='fk_question_sport') THEN
        ALTER TABLE question ADD CONSTRAINT fk_question_sport FOREIGN KEY (sport_id) REFERENCES sport(id);
    END IF;
END $$;

-- Step 3: Remove old columns if present
ALTER TABLE question DROP COLUMN IF EXISTS format_id;
ALTER TABLE question DROP COLUMN IF EXISTS sport;

-- Step 4: Remove all existing questions
DELETE FROM question;

-- Step 5: Insert 6 doubles questions for Badminton
INSERT INTO question (text, category, order_index, min_value, max_value, sport_id, format)
VALUES
('How confident are you in your ability to serve accurately in doubles?', 'serving', 1, 1, 5, (SELECT id FROM sport WHERE name = 'Badminton'), 'DOUBLES'),
('How well do you coordinate with your partner during rallies?', 'teamwork', 2, 1, 5, (SELECT id FROM sport WHERE name = 'Badminton'), 'DOUBLES'),
('How effective are your net interceptions and front-court play in doubles?', 'net_play', 3, 1, 5, (SELECT id FROM sport WHERE name = 'Badminton'), 'DOUBLES'),
('How comfortable are you with defensive formations and quick transitions in doubles?', 'defense', 4, 1, 5, (SELECT id FROM sport WHERE name = 'Badminton'), 'DOUBLES'),
('How strong is your smash and attacking play from the back-court in doubles?', 'attack', 5, 1, 5, (SELECT id FROM sport WHERE name = 'Badminton'), 'DOUBLES'),
('How well do you communicate and strategize with your partner during a match?', 'communication', 6, 1, 5, (SELECT id FROM sport WHERE name = 'Badminton'), 'DOUBLES');
