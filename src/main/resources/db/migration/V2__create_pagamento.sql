CREATE TABLE IF NOT EXISTS pagamento (
                                      id INTEGER PRIMARY KEY,
                                      pedido_id INTEGER,
                                      data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      status_pagamento_id INTEGER,
                                      valor NUMERIC(10, 2) NOT NULL,
                                      qr_code_pix VARCHAR(250) NOT NULL,
                                      FOREIGN KEY (status_pagamento_id) REFERENCES status_pagamento (id)
);