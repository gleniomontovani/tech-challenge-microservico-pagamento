INSERT INTO status_pagamento (id, nome) VALUES (1, 'Pendente') ON CONFLICT DO NOTHING;
INSERT INTO status_pagamento (id, nome) VALUES (2, 'Aprovado') ON CONFLICT DO NOTHING;
INSERT INTO status_pagamento (id, nome) VALUES (3, 'Recusado') ON CONFLICT DO NOTHING;