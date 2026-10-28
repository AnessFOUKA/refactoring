package fr.efrei.transfret;

public class RemiseParticulier implements PolitiqueRemise {

    @Override
    public double appliquer(double prix) {
        return prix;
    }
}
