package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import entidades.CambioMoneda;

public class ServicioCambioMoneda {

    public static List<CambioMoneda> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            Stream<String> lineas = Files.lines(Paths.get(nombreArchivo));
            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new CambioMoneda(textos[0], LocalDate.parse(textos[1], formatoFecha),
                            Double.parseDouble(textos[2])))
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public static List<String> getMonedas(List<CambioMoneda> datos) {
        return datos.stream()
                .map(CambioMoneda::getMoneda)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<CambioMoneda> filtrar(String moneda, LocalDate desde, LocalDate hasta,
            List<CambioMoneda> datos) {
        return datos.stream()
                .filter(dato -> dato.getMoneda().equals(moneda)
                        && !dato.getFecha().isBefore(desde) && !dato.getFecha().isAfter(hasta))
                .collect(Collectors.toList());

    }

    public static Par<List<LocalDate>, List<Double>> extraer(List<CambioMoneda> datos){
        var datosOrdenados=datos.stream()
            .sorted(Comparator.comparing(CambioMoneda::getFecha))
            .collect(Collectors.toList());

        var fechas=datosOrdenados.stream().map(CambioMoneda::getFecha).collect(Collectors.toList());
        var cambios=datosOrdenados.stream().map(CambioMoneda::getCambio).collect(Collectors.toList());

        return new Par<>(fechas, cambios);
    }

    public static double getPromedio(List<Double> cambios) {
        return cambios.isEmpty() ? 0 : cambios.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public static double getDesviacionEstandar(List<Double> cambios) {
        var promedio = getPromedio(cambios);
        return cambios.isEmpty() ? 0
                : Math.sqrt(cambios.stream()
                        .mapToDouble(cambio -> Math.pow(cambio - promedio, 2))
                        .average()
                        .orElse(0));
    }

    public static double getMaximo(List<Double> cambios) {
        return cambios.isEmpty() ? 0 : cambios.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    public static double getMinimo(List<Double> cambios) {
        return cambios.isEmpty() ? 0 : cambios.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    public static double getMediana(List<Double> cambios) {
        if (cambios.isEmpty()) {
            return 0;
        }
        var cambiosOrdenados = cambios.stream().sorted().collect(Collectors.toList());

        var n = cambiosOrdenados.size();
        return n % 2 == 0 ? (cambiosOrdenados.get(n / 2 - 1) + cambiosOrdenados.get(n / 2)) / 2
                : cambiosOrdenados.get(n / 2);
    }

    public static double getModa(List<Double> cambios) {
        return cambios.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    public static Map<String, Double> getEstadisticas(String moneda, LocalDate desde, LocalDate hasta,
            List<CambioMoneda> datos) {
        var datosFiltrados = filtrar(moneda, desde, hasta, datos);
        var cambios = datosFiltrados.stream().map(CambioMoneda::getCambio).collect(Collectors.toList());

        Map<String, Double> estadisticas=new LinkedHashMap<>();
        estadisticas.put("Promedio:", getPromedio(cambios));
        estadisticas.put("Desviación estandar:", getDesviacionEstandar(cambios));
        estadisticas.put("Máximo:", getMaximo(cambios));
        estadisticas.put("Mínimo:", getMinimo(cambios));
        estadisticas.put("Mediana:", getMediana(cambios));
        estadisticas.put("Moda:", getModa(cambios));

        return estadisticas;
    }

}
