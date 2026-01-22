-- Create diffusions table
CREATE TABLE IF NOT EXISTS diffusions (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    trip_id INTEGER NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT diffusions_status_check CHECK (status IN ('EN_ATTENTE', 'PAYE', 'ANNULE'))
);

-- Create indexes for better performance
CREATE INDEX idx_diffusions_client_id ON diffusions(client_id);
CREATE INDEX idx_diffusions_trip_id ON diffusions(trip_id);
CREATE INDEX idx_diffusions_status ON diffusions(status);
CREATE INDEX idx_diffusions_payment_date ON diffusions(payment_date);

-- Add comments
COMMENT ON TABLE diffusions IS 'Table to manage diffusions for society clients';
COMMENT ON COLUMN diffusions.client_id IS 'Reference to the client (society type)';
COMMENT ON COLUMN diffusions.trip_id IS 'Reference to the trip';
COMMENT ON COLUMN diffusions.amount IS 'Amount to be paid for the diffusion';
COMMENT ON COLUMN diffusions.payment_date IS 'Date when the payment was made';
COMMENT ON COLUMN diffusions.status IS 'Status of the diffusion: EN_ATTENTE, PAYE, ANNULE';
