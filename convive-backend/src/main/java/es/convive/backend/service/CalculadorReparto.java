package es.convive.backend.service;

import es.convive.backend.exception.DatosGastoException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CalculadorReparto {

    public Map<Long, BigDecimal> repartir(BigDecimal importe, List<Long> identificadoresOrdenados) {
        if (identificadoresOrdenados == null || identificadoresOrdenados.isEmpty()) {
            throw new DatosGastoException("Debe existir al menos un participante");
        }
        final long centimos;
        try {
            centimos = importe.setScale(2, RoundingMode.UNNECESSARY)
                    .movePointRight(2).longValueExact();
        } catch (ArithmeticException ex) {
            throw new DatosGastoException("El importe debe expresarse con como máximo dos decimales");
        }
        if (centimos <= 0) {
            throw new DatosGastoException("El importe debe ser mayor que cero");
        }

        long base = centimos / identificadoresOrdenados.size();
        int sobrantes = (int) (centimos % identificadoresOrdenados.size());
        Map<Long, BigDecimal> reparto = new LinkedHashMap<>();
        for (int i = 0; i < identificadoresOrdenados.size(); i++) {
            long cantidad = base + (i < sobrantes ? 1 : 0);
            reparto.put(identificadoresOrdenados.get(i), BigDecimal.valueOf(cantidad, 2));
        }
        return reparto;
    }
}
