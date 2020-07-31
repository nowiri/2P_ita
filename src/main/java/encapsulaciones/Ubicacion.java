package encapsulaciones;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Ubicacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float longitud;
    private float latitud;

    @OneToOne(fetch = FetchType.EAGER)
    private Formulario formulario;

    public Ubicacion() {}

    public Ubicacion(float longitud, float latitud, Formulario formulario) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.formulario = formulario;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }
}
