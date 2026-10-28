package fr.efrei.transfret;

public class AssembleurPrix {

    public PrixCalculable assembler(StrategiesTarification strategies) {
        PrixCalculable prix = new TarifDeBasePrix(strategies);
        prix = new SupplementFragile(prix);
        prix = new SupplementDangereux(prix);
        prix = new SupplementRefrigere(prix);
        prix = new SupplementHorsGabarit(prix);
        prix = new SupplementUrgence(prix);
        prix = new DecorateurRemise(prix, strategies);
        return new DecorateurAssurance(prix);
    }
}
