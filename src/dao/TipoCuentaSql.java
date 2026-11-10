package dao;

/**
 * Fragmento SQL que normaliza el nombre del tipo de cuenta para los
 * reportes. Asi "Activo", "ACTIVO " o "Egreso" se reconocen igual,
 * sin importar mayusculas ni espacios.
 *
 * Resultado: ACTIVO, PASIVO, PATRIMONIO, INGRESO o GASTO
 * (EGRESO se trata como GASTO, tal como lo nombra el enunciado).
 */
final class TipoCuentaSql {

    static final String PLACEHOLDER = "{TIPO}";

    static final String EXPRESION = """
            CASE
                WHEN UPPER(TRIM(TC.NOMBRE)) IN ('GASTO', 'GASTOS', 'EGRESO', 'EGRESOS')
                    THEN 'GASTO'
                WHEN UPPER(TRIM(TC.NOMBRE)) IN ('INGRESO', 'INGRESOS')
                    THEN 'INGRESO'
                WHEN UPPER(TRIM(TC.NOMBRE)) IN ('PATRIMONIO', 'CAPITAL')
                    THEN 'PATRIMONIO'
                ELSE UPPER(TRIM(TC.NOMBRE))
            END""";

    private TipoCuentaSql() {
    }

    static String aplicar(String sql) {
        return sql.replace(PLACEHOLDER, EXPRESION);
    }
}
