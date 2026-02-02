package ec.edu.monster.servicio;
import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.modelo.Movimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author leito
 */
public class EurekaService {
    
    /**
     * Obtiene todos los números de cuenta activos del sistema.
     * @return Lista de números de cuenta
     */
    public List<String> obtenerTodasLasCuentas() {
        Connection cn = null;
        List<String> cuentas = new ArrayList<>();
        
        String sql = "SELECT chr_cuencodigo FROM Cuenta WHERE vch_cuenestado = 'ACTIVO' ORDER BY chr_cuencodigo";
        
        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                cuentas.add(rs.getString("chr_cuencodigo"));
            }
            
            rs.close();
            pstm.close();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las cuentas: " + e.getMessage());
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                // Ignorar excepción al cerrar la conexión
            }
        }
        
        return cuentas;
    }
    
    public double getMonto(String cuenta){
        Connection cn = null;
        double saldo = 0.0;
        
        String sql = "SELECT dec_cuensaldo "
                        + "FROM Cuenta "
                        + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' ";
        
        try {
            // 1. Obtener la conexión
            cn = AccesoDB.getConnection();
            
            // 2. Preparar la sentencia
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            
            // 3. Ejecutar y obtener el resultado
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                // Leer el saldo del primer y único registro
                saldo = rs.getDouble("dec_cuensaldo");
            } else {
                // Si la cuenta no existe o no está activa, lanzamos una excepción 
                // para indicar que no se encontró el saldo.
                throw new SQLException("ERROR, cuenta " + cuenta + " no existe o no está activa.");
            }
            
            // 4. Cerrar recursos
            rs.close();
            pstm.close();
            
        } catch (SQLException e) {
            // Capturar la excepción y relanzarla como RuntimeException para la capa superior
            throw new RuntimeException("Error al obtener el saldo de la cuenta: " + e.getMessage());
        } finally {
            // 5. Cerrar la conexión
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                // Ignorar excepción al cerrar la conexión
            }
        }
        
        return saldo;
    }
    
    public List<Movimiento> leerMovimientos(String cuenta) {
        Connection cn = null;
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT \n"
                + " m.chr_cuencodigo cuenta, \n"
                + " m.int_movinumero nromov, \n"
                + " m.dtt_movifecha fecha, \n"
                + " t.vch_tipodescripcion tipo, \n"
                + " t.vch_tipoaccion accion, \n"
                + " m.dec_moviimporte importe \n"
                + "FROM TipoMovimiento t INNER JOIN Movimiento m \n"
                + "ON t.chr_tipocodigo = m.chr_tipocodigo \n"
                + "WHERE m.chr_cuencodigo = ? \n"
                + "ORDER BY m.int_movinumero DESC"; // Ordenar por fecha descendente
        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Movimiento rec = new Movimiento();
                rec.setCuenta(rs.getString("cuenta"));
                rec.setNromov(rs.getInt("nromov"));
                rec.setFecha(rs.getDate("fecha"));
                rec.setTipo(rs.getString("tipo"));
                rec.setAccion(rs.getString("accion"));
                rec.setImporte(rs.getDouble("importe"));
                lista.add(rec);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                // Ignorar cualquier excepción al cerrar la conexión
            }
        }
        return lista;
    }

    public void registrarDeposito(String cuenta, double importe, String codEmp) {
        Connection cn = null;
        try {
            // Obtener la conexión
            cn = AccesoDB.getConnection();
            // Habilitar la transacción
            cn.setAutoCommit(false);

            // Paso 1: Leer datos de la cuenta
            String sql = "SELECT dec_cuensaldo, int_cuencontmov "
                       + "FROM Cuenta "
                       + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' "
                       + "FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("ERROR, cuenta no existe o no está activa");
            }
            double saldo = rs.getDouble("dec_cuensaldo");
            int cont = rs.getInt("int_cuencontmov");
            rs.close();
            pstm.close();

            // Paso 2: Obtener el siguiente número de movimiento
            sql = "SELECT MAX(int_movinumero) + 1 AS siguiente_numero "
                + "FROM Movimiento "
                + "WHERE chr_cuencodigo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            rs = pstm.executeQuery();
            int siguienteNumero = 1; // Default to 1 if no rows exist
            if (rs.next()) {
                siguienteNumero = rs.getInt("siguiente_numero");
            }
            rs.close();
            pstm.close();

            // Paso 3: Actualizar la cuenta
            saldo += importe;
            cont++;
            sql = "UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? "
                + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'";
            pstm = cn.prepareStatement(sql);
            pstm.setDouble(1, saldo);
            pstm.setInt(2, cont);
            pstm.setString(3, cuenta);
            pstm.executeUpdate();
            pstm.close();

            // Paso 4: Registrar movimiento
            sql = "INSERT INTO Movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) "
                + "VALUES(?, ?, SYSDATE(), ?, '003', ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            pstm.setInt(2, siguienteNumero);  // Usar el siguiente número generado
            pstm.setString(3, codEmp);
            pstm.setDouble(4, importe);
            pstm.executeUpdate();

            // Confirmar la transacción
            cn.commit();
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (Exception el) {
            }
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            try {
                cn.rollback();
            } catch (Exception el) {
            }
            throw new RuntimeException("ERROR, en el proceso registrar depósito, intentelo más tarde.");
        } finally {
            try {
                cn.close();
            } catch (Exception e) {
            }
        }
    }
    
    // ----------------------------------------------------------------------------------
    // NUEVA FUNCIÓN: RETIRO
    // ----------------------------------------------------------------------------------
    public void registrarRetiro(String cuenta, double importe, String codEmp) {
        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // --- Paso 1: Leer datos de la cuenta (SALDO Y CONTADOR) y bloquear la fila
            String sql = "SELECT dec_cuensaldo, int_cuencontmov "
                       + "FROM Cuenta "
                       + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' "
                       + "FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("ERROR, cuenta no existe o no está activa");
            }
            double saldo = rs.getDouble("dec_cuensaldo");
            int cont = rs.getInt("int_cuencontmov");
            rs.close();
            pstm.close();
            
            // --- VERIFICACIÓN DE SALDO
            if (saldo < importe) {
                throw new SQLException("ERROR, saldo insuficiente para el retiro. Saldo actual: " + saldo);
            }

            // --- Paso 2: Obtener el siguiente número de movimiento
            sql = "SELECT MAX(int_movinumero) + 1 AS siguiente_numero FROM Movimiento WHERE chr_cuencodigo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            rs = pstm.executeQuery();
            int siguienteNumero = 1; 
            if (rs.next()) {
                siguienteNumero = rs.getInt("siguiente_numero");
            }
            rs.close();
            pstm.close();

            // --- Paso 3: Actualizar la cuenta (SALDO Y CONTADOR)
            saldo -= importe; // Restar el importe
            cont++;
            sql = "UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? "
                + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'";
            pstm = cn.prepareStatement(sql);
            pstm.setDouble(1, saldo);
            pstm.setInt(2, cont);
            pstm.setString(3, cuenta);
            pstm.executeUpdate();
            pstm.close();

            // --- Paso 4: Registrar movimiento (TIPO '004' Retiro)
            sql = "INSERT INTO Movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) "
                + "VALUES(?, ?, SYSDATE(), ?, '004', ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            pstm.setInt(2, siguienteNumero);
            pstm.setString(3, codEmp);
            pstm.setDouble(4, importe);
            pstm.executeUpdate();

            // Confirmar la transacción
            cn.commit();
        } catch (SQLException e) {
            try { cn.rollback(); } catch (Exception el) {}
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            try { cn.rollback(); } catch (Exception el) {}
            throw new RuntimeException("ERROR, en el proceso registrar retiro, intentelo más tarde.");
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
    }
    
    // ----------------------------------------------------------------------------------
    // NUEVA FUNCIÓN: TRANSFERENCIA
    // ----------------------------------------------------------------------------------
    /**
     * Registra una transferencia entre dos cuentas.
     * @param cuentaOrigen
     * @param cuentaDestino
     * @param importe
     * @param codEmp
     */
    public void registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe, String codEmp) {
        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // --- PASO 1: LECTURA DE CUENTA ORIGEN (SALDO Y CONTADOR) y bloqueo
            String sqlOrigen = "SELECT dec_cuensaldo, int_cuencontmov "
                             + "FROM Cuenta "
                             + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' "
                             + "FOR UPDATE";
            PreparedStatement pstmOrigen = cn.prepareStatement(sqlOrigen);
            pstmOrigen.setString(1, cuentaOrigen);
            ResultSet rsOrigen = pstmOrigen.executeQuery();
            if (!rsOrigen.next()) {
                throw new SQLException("ERROR, cuenta origen no existe o no está activa");
            }
            double saldoOrigen = rsOrigen.getDouble("dec_cuensaldo");
            int contOrigen = rsOrigen.getInt("int_cuencontmov");
            rsOrigen.close();
            pstmOrigen.close();

            // --- PASO 1.1: LECTURA DE CUENTA DESTINO (SALDO Y CONTADOR) y bloqueo
            String sqlDestino = "SELECT dec_cuensaldo, int_cuencontmov "
                             + "FROM Cuenta "
                             + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' "
                             + "FOR UPDATE";
            PreparedStatement pstmDestino = cn.prepareStatement(sqlDestino);
            pstmDestino.setString(1, cuentaDestino);
            ResultSet rsDestino = pstmDestino.executeQuery();
            if (!rsDestino.next()) {
                throw new SQLException("ERROR, cuenta destino no existe o no está activa");
            }
            double saldoDestino = rsDestino.getDouble("dec_cuensaldo");
            int contDestino = rsDestino.getInt("int_cuencontmov");
            rsDestino.close();
            pstmDestino.close();
            
            // --- VERIFICACIÓN DE SALDO
            if (saldoOrigen < importe) {
                throw new SQLException("ERROR, saldo insuficiente en cuenta origen.");
            }

            // --- PASO 2: Obtener siguientes números de movimiento (Origen y Destino)
            // Origen
            sqlOrigen = "SELECT MAX(int_movinumero) + 1 AS siguiente_numero FROM Movimiento WHERE chr_cuencodigo = ?";
            PreparedStatement pstmSiguienteOrigen = cn.prepareStatement(sqlOrigen);
            pstmSiguienteOrigen.setString(1, cuentaOrigen);
            ResultSet rsSiguienteOrigen = pstmSiguienteOrigen.executeQuery();
            int siguienteNumeroOrigen = 1; 
            if (rsSiguienteOrigen.next()) {
                siguienteNumeroOrigen = rsSiguienteOrigen.getInt("siguiente_numero");
            }
            rsSiguienteOrigen.close();
            pstmSiguienteOrigen.close();
            
            // Destino
            sqlDestino = "SELECT MAX(int_movinumero) + 1 AS siguiente_numero FROM Movimiento WHERE chr_cuencodigo = ?";
            PreparedStatement pstmSiguienteDestino = cn.prepareStatement(sqlDestino);
            pstmSiguienteDestino.setString(1, cuentaDestino);
            ResultSet rsSiguienteDestino = pstmSiguienteDestino.executeQuery();
            int siguienteNumeroDestino = 1; 
            if (rsSiguienteDestino.next()) {
                siguienteNumeroDestino = rsSiguienteDestino.getInt("siguiente_numero");
            }
            rsSiguienteDestino.close();
            pstmSiguienteDestino.close();

            // --- PASO 3: Actualizar las cuentas
            // Origen (Salida)
            saldoOrigen -= importe;
            contOrigen++;
            sqlOrigen = "UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? "
                      + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'";
            PreparedStatement pstmUpdateOrigen = cn.prepareStatement(sqlOrigen);
            pstmUpdateOrigen.setDouble(1, saldoOrigen);
            pstmUpdateOrigen.setInt(2, contOrigen);
            pstmUpdateOrigen.setString(3, cuentaOrigen);
            pstmUpdateOrigen.executeUpdate();
            pstmUpdateOrigen.close();
            
            // Destino (Entrada)
            saldoDestino += importe;
            contDestino++;
            sqlDestino = "UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? "
                       + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'";
            PreparedStatement pstmUpdateDestino = cn.prepareStatement(sqlDestino);
            pstmUpdateDestino.setDouble(1, saldoDestino);
            pstmUpdateDestino.setInt(2, contDestino);
            pstmUpdateDestino.setString(3, cuentaDestino);
            pstmUpdateDestino.executeUpdate();
            pstmUpdateDestino.close();


            // --- PASO 4: Registrar movimientos
            
            // Movimiento Origen (TIPO '009' Transferencia Salida, asumiendo ese código)
            sqlOrigen = "INSERT INTO Movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                      + "VALUES(?, ?, SYSDATE(), ?, '009', ?, ?)";
            PreparedStatement pstmMovOrigen = cn.prepareStatement(sqlOrigen);
            pstmMovOrigen.setString(1, cuentaOrigen);
            pstmMovOrigen.setInt(2, siguienteNumeroOrigen);
            pstmMovOrigen.setString(3, codEmp);
            pstmMovOrigen.setDouble(4, importe);
            pstmMovOrigen.setString(5, cuentaDestino); // Referencia a la cuenta destino
            pstmMovOrigen.executeUpdate();
            pstmMovOrigen.close();
            
            // Movimiento Destino (TIPO '008' Transferencia Ingreso, asumiendo ese código)
            sqlDestino = "INSERT INTO Movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                       + "VALUES(?, ?, SYSDATE(), ?, '008', ?, ?)";
            PreparedStatement pstmMovDestino = cn.prepareStatement(sqlDestino);
            pstmMovDestino.setString(1, cuentaDestino);
            pstmMovDestino.setInt(2, siguienteNumeroDestino);
            pstmMovDestino.setString(3, codEmp);
            pstmMovDestino.setDouble(4, importe);
            pstmMovDestino.setString(5, cuentaOrigen); // Referencia a la cuenta origen
            pstmMovDestino.executeUpdate();
            pstmMovDestino.close();

            // Confirmar la transacción
            cn.commit();
        } catch (SQLException e) {
            try { cn.rollback(); } catch (Exception el) {}
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            try { cn.rollback(); } catch (Exception el) {}
            throw new RuntimeException("ERROR, en el proceso registrar transferencia, intentelo más tarde.");
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) {}
        }
    }
}
