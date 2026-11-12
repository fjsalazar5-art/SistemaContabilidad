-- =========================================================
-- MODULO BANCOS - Consultas para el video (Francisco Guerrón)
-- Ejecutar en SQL Developer conectado al esquema PROYECTO
-- =========================================================

-- ---------------------------------------------------------
-- 0) ANTES DE GRABAR: dejar las tablas de Bancos en blanco
--    (borra solo datos de prueba de Bancos, en orden por FK)
--    Si ya enviaste movimientos a Contabilidad, el compañero
--    de Contabilidad debe limpiar COMPROBANTE / COMPROBANTE_DETALLE.
-- ---------------------------------------------------------
-- DELETE FROM MOV_BANCARIO_DET;
-- DELETE FROM MOV_BANCARIO;
-- DELETE FROM CUENTA_BANCARIA;
-- DELETE FROM TIPO_TRANSACCION;
-- DELETE FROM COMPROBANTE_DETALLE WHERE ID_COMPROBANTE IN
--   (SELECT ID_COMPROBANTE FROM COMPROBANTE WHERE OBSERVACION LIKE 'MOV. BANCARIO%');
-- DELETE FROM COMPROBANTE WHERE OBSERVACION LIKE 'MOV. BANCARIO%';
-- DELETE FROM CUENTA_CONTABLE WHERE NOMBRE = 'BANCOS';
-- COMMIT;

-- ---------------------------------------------------------
-- 1) PASO 11 - Pantalla simple 1: Tipo de Transacción
-- ---------------------------------------------------------
SELECT * FROM TIPO_TRANSACCION ORDER BY ID_TIPO_TRANSACCION;

-- ---------------------------------------------------------
-- 2) PASO 12 - Pantalla simple 2: Cuenta Bancaria
-- ---------------------------------------------------------
SELECT * FROM CUENTA_BANCARIA ORDER BY ID_CUENTA_BANCO;

-- ---------------------------------------------------------
-- 3) PASO 13f - Pantalla compleja: cabecera y detalle
-- ---------------------------------------------------------
SELECT M.ID_MOVIMIENTO, M.FECHA, C.DESCRIPCION AS CUENTA_BANCARIA, M.DESCRIPCION
FROM MOV_BANCARIO M
JOIN CUENTA_BANCARIA C ON C.ID_CUENTA_BANCO = M.ID_CUENTA_BANCO
ORDER BY M.ID_MOVIMIENTO;

SELECT D.ID_MOV_DET, D.ID_MOVIMIENTO, D.FECHA,
       T.NOMBRE AS TIPO_TRANSACCION, T.SIGNO, D.VALOR AS CANTIDAD
FROM MOV_BANCARIO_DET D
JOIN TIPO_TRANSACCION T ON T.ID_TIPO_TRANSACCION = D.ID_TIPO_TRANSACCION
ORDER BY D.ID_MOVIMIENTO, D.ID_MOV_DET;

-- ---------------------------------------------------------
-- 4) PASO 14 - Integración Bancos -> Contabilidad
-- ---------------------------------------------------------
SELECT * FROM COMPROBANTE
WHERE OBSERVACION LIKE 'MOV. BANCARIO%'
ORDER BY ID_COMPROBANTE;

SELECT CD.ID_COMPROBANTE, CC.NOMBRE AS CUENTA_CONTABLE, CD.DEBE, CD.HABER
FROM COMPROBANTE_DETALLE CD
JOIN CUENTA_CONTABLE CC ON CC.ID_CUENTA = CD.ID_CUENTA
JOIN COMPROBANTE CO ON CO.ID_COMPROBANTE = CD.ID_COMPROBANTE
WHERE CO.OBSERVACION LIKE 'MOV. BANCARIO%'
ORDER BY CD.ID_COMPROBANTE, CD.ID_COMP_DET;
