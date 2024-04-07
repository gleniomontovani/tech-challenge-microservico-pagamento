CREATE TABLE IF NOT EXISTS pagamento (
                                      id INTEGER PRIMARY KEY,
                                      pedido_id INTEGER,
                                      data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      status_pagamento_id INTEGER,
                                      valor NUMERIC(10, 2) NOT NULL,
                                      FOREIGN KEY (pedido_id) REFERENCES pedido (id),
                                      FOREIGN KEY (status_pagamento_id) REFERENCES status_pagamento (id)
);