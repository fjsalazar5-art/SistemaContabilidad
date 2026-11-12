# Guion video — Módulo BANCOS (Francisco Guerrón) — máx. 6 min

Antes de grabar: cámara encendida, nombre visible, que NO tape la fecha/hora del PC.
Tener abierto SQL Developer con `database/bancos_video.sql`.

| Paso | Qué hacer en el sistema | Qué decir (resumen) |
|---|---|---|
| 1–6 | (Lo hace el login común) Entrar con el usuario nuevo → menú principal | "Soy Francisco Guerrón, módulo Bancos" |
| 7 | Menú → **Tipo Transacción** | "Pantalla simple 1: tipos de transacción con signo + (ingreso) o – (egreso)" |
| 8 | Nombre `Tipo Transaccion A`, signo **+** → Guardar. Nuevo → `Tipo Transaccion B`, signo **–** → Guardar | "El ID lo genera la secuencia SEQ_TIPO_TRANSACCION" |
| 9 | Buscar → escribir `A` (o su ID) → cambiar nombre a `Tipo Transaccion X` → Modificar | |
| 10 | Buscar → `B` → Eliminar → Sí | (la guía dice "registro C"; se elimina el B, que es el que existe) |
| 11 | SQL Developer: `SELECT * FROM TIPO_TRANSACCION;` → se ve solo X | |
| 12 | Menú → **Cuenta Bancaria**: `Cuenta Bancaria A` (N° 2200112233, titular), `Cuenta Bancaria B` → buscar A → `Cuenta Bancaria X` → buscar B → eliminar → `SELECT * FROM CUENTA_BANCARIA;` | |
| 13a | Menú → **Movimiento Bancario**. Cabecera: cuenta `Cuenta Bancaria X`, descripción. Detalle: tipo `Tipo Transaccion X` cantidad 100 → Agregar; otra vez `Tipo Transaccion X` cantidad 50 → Agregar → **Guardar** | "Se guarda cabecera y detalle en una sola transacción (commit/rollback)" |
| 13b | Cerrar la ventana y volver a abrirla | |
| 13c | Buscar → escribir `X` (o el N° de movimiento) | |
| 13d | Seleccionar la línea de 50 → **Eliminar Detalle** | |
| 13e | Seleccionar la línea sobrante → cambiar Cantidad a 250 → **Modificar Detalle** → **Modificar** | "Ahora se guarda en la base" |
| 13f | SQL Developer: SELECTs de MOV_BANCARIO y MOV_BANCARIO_DET | |
| 14 | En el movimiento → **Enviar a Contabilidad** → contrapartida (ej. CAJA) → mensaje con N° de comprobante. Mostrarlo en Contabilidad > Comprobantes (Buscar ese N°) y con el SELECT de integración | "Depósito: DEBE BANCOS / HABER contrapartida. La cuenta BANCOS se crea sola si no existe" |
| 15 | Menú → **Rep. Saldo por Cuenta** | |
| 16 | Menú → **Rep. Movimientos Fechas** → rango → Generar | |

Tip: practica una vez con cronómetro; los pasos 8–12 son los que más tiempo comen.
