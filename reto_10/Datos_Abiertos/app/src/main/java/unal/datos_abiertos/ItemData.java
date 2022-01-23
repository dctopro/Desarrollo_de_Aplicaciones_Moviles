package unal.datos_abiertos;

public class ItemData {
    public String nombregrupoTextView, codigogrupoTextView, institucionTextView;

    ItemData(String nombre_grupo, String codigo, String nombre_ins){
        this.nombregrupoTextView=nombre_grupo;
        this.codigogrupoTextView=codigo;
        this.institucionTextView=nombre_ins;
    }
}
