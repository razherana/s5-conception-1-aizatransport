CREATE TABLE produits_extras (
    id
    nom
    prix
)

CREATE TABLE facture_extra (
    id
    client
    date
    facture_extra_fille[]
)

CREATE TABLE facture_extra_fille(
    id
    facture_extra_id
    produits_extra_id
    quantite
    prix_unitaire
)