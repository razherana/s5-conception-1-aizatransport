-- Migration for Diffusion and DiffusionFille tables
-- Add designation column to diffusions table
ALTER TABLE diffusions ADD COLUMN IF NOT EXISTS designation VARCHAR(255);

-- Create diffusion_filles table for partial payments
CREATE TABLE IF NOT EXISTS diffusion_filles (
  id SERIAL PRIMARY KEY,
  diffusion_id INTEGER NOT NULL REFERENCES diffusions(id) ON DELETE CASCADE,
  revenue_id INTEGER REFERENCES revenues(id) ON DELETE SET NULL,
  amount DECIMAL(10, 2) NOT NULL,
  payment_date TIMESTAMP NOT NULL,
  payment_method VARCHAR(20) NOT NULL,
  reference VARCHAR(100),
  notes VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_diffusion_filles_diffusion_id ON diffusion_filles(diffusion_id);
CREATE INDEX IF NOT EXISTS idx_diffusion_filles_revenue_id ON diffusion_filles(revenue_id);
CREATE INDEX IF NOT EXISTS idx_diffusion_filles_payment_date ON diffusion_filles(payment_date);

-- Add comments for documentation
COMMENT ON TABLE diffusion_filles IS 'Partial payments for diffusions - allows clients to pay in multiple installments';
COMMENT ON COLUMN diffusion_filles.diffusion_id IS 'Reference to the parent diffusion';
COMMENT ON COLUMN diffusion_filles.revenue_id IS 'Optional reference to the revenue entry';
COMMENT ON COLUMN diffusion_filles.amount IS 'Amount paid in this installment';
COMMENT ON COLUMN diffusion_filles.payment_date IS 'Date when this payment was made';
COMMENT ON COLUMN diffusion_filles.payment_method IS 'Payment method used (ESPECES, CARTE_BANCAIRE, VIREMENT, CHEQUE, MOBILE_MONEY)';
COMMENT ON COLUMN diffusions.designation IS 'Description or designation of the diffusion';
