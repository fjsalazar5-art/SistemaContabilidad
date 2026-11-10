-- =========================================================
-- MIGRACION para una base YA CREADA (esquema PROYECTO)
-- Aplica los mismos cambios que SistemaContabilidad_DDL.sql
-- sin borrar nada. Si una linea da "name already used" o
-- "column already exists", ese cambio ya estaba hecho: omitelo.
-- =========================================================

-- 1) FACTURA_DETALLE: SUBTOTAL calculado por Oracle
ALTER TABLE FACTURA_DETALLE ADD (
    SUBTOTAL NUMBER(12,2)
        GENERATED ALWAYS AS (ROUND(CANTIDAD * PRECIO, 2)) VIRTUAL
);

-- 2) COMPROBANTE_DETALLE: debe/haber obligatorios y coherentes
UPDATE COMPROBANTE_DETALLE
   SET DEBE = NVL(DEBE, 0), HABER = NVL(HABER, 0);
ALTER TABLE COMPROBANTE_DETALLE MODIFY (DEBE NOT NULL, HABER NOT NULL);
ALTER TABLE COMPROBANTE_DETALLE ADD CONSTRAINT CK_COMP_DET_DEBE_HABER
    CHECK (DEBE >= 0 AND HABER >= 0 AND (DEBE = 0 OR HABER = 0));

-- 3) USUARIO: nombre unico y rol
ALTER TABLE USUARIO ADD CONSTRAINT UK_USUARIO_NOMBRE UNIQUE (NOMBRE_USUARIO);
ALTER TABLE USUARIO ADD (ROL VARCHAR2(20) DEFAULT 'USUARIO' NOT NULL);
ALTER TABLE USUARIO ADD CONSTRAINT CK_USUARIO_ROL
    CHECK (ROL IN ('ADMIN', 'USUARIO'));

-- 4) TIPO_CUENTA: nombre unico
ALTER TABLE TIPO_CUENTA ADD CONSTRAINT UK_TIPO_CUENTA_NOMBRE UNIQUE (NOMBRE);

-- 5) TIPO_TRANSACCION: signo valido
ALTER TABLE TIPO_TRANSACCION ADD CONSTRAINT CK_TIPO_TRANS_SIGNO
    CHECK (SIGNO IN ('+', '-'));

-- 6) Limpiar papelera de reciclaje
PURGE RECYCLEBIN;

COMMIT;
