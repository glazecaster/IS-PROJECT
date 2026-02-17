package controller;

import view.*;
import model.*;
import java.io.*;
import javax.swing.*;

public class LoginController {
    public LoginView v;

    public LoginController(LoginView v) {
        this.v = v;

        v.btnModoRegistro.addActionListener(e -> {
            v.lblConfirmar.setVisible(true);
            v.txtConfirmar.setVisible(true);
            v.lblTipo.setVisible(true);
            v.rbComensal.setVisible(true);
            v.rbAdmin.setVisible(true);
            v.btnRegistrar.setVisible(true);
            v.btnVolverLogin.setVisible(true);
            v.btnEntrar.setVisible(false);
            v.btnModoRegistro.setVisible(false);
            v.revalidate();
        });

        v.btnVolverLogin.addActionListener(e -> {
            v.lblConfirmar.setVisible(false);
            v.txtConfirmar.setVisible(false);
            v.lblTipo.setVisible(false);
            v.rbComensal.setVisible(false);
            v.rbAdmin.setVisible(false);
            v.btnRegistrar.setVisible(false);
            v.btnVolverLogin.setVisible(false);
            v.btnEntrar.setVisible(true);
            v.btnModoRegistro.setVisible(true);
            v.revalidate();
        });

        v.btnRegistrar.addActionListener(e -> {
            String c = v.txtCedula.getText();
            String p = new String(v.txtClave.getPassword());
            String p2 = new String(v.txtConfirmar.getPassword());

            if (!v.rbComensal.isSelected() && !v.rbAdmin.isSelected()) {
                JOptionPane.showMessageDialog(null, "ERROR: Debes seleccionar Comensal o Administrador");
                return;
            }

            if (c.length() < 5) {
                JOptionPane.showMessageDialog(null, "Esa cedula es muy corta, minimo 5 numeros");
                return;
            }

            if (p.length() < 8) {
                JOptionPane.showMessageDialog(null, "Clave insegura, ponle minimo 8 caracteres");
                return;
            }

            if (!p.equals(p2)) {
                JOptionPane.showMessageDialog(null, "Las claves no son iguales, fijate bien");
                return;
            }

            // chequeo de cedula duplicada
            boolean yaEsta = false;
            try {
                File f = new File("usuarios.txt");
                if (f.exists()) {
                    BufferedReader checker = new BufferedReader(new FileReader(f));
                    String linea;
                    while ((linea = checker.readLine()) != null) {
                        String[] d = linea.split(",");
                        if (d[0].equals(c)) {
                            yaEsta = true;
                            break;
                        }
                    }
                    checker.close();
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            if (yaEsta) {
                JOptionPane.showMessageDialog(null, "Esa cedula ya esta ocupada");
            } else {
                try {
                    String t = v.rbComensal.isSelected() ? "C" : "A";
                    FileWriter fff = new FileWriter("usuarios.txt", true);
                    fff.write(c + "," + p + ",9.5," + t + "\n");
                    fff.close();
                    JOptionPane.showMessageDialog(null, "Registrado como " + (t.equals("C") ? "Comensal" : "Administrador"));
                    v.grupoTipo.clearSelection();
                    v.btnVolverLogin.doClick(); 
                } catch (Exception exx) { exx.printStackTrace(); }
            }
        });

        v.btnEntrar.addActionListener(e -> {
            try {
                BufferedReader read = new BufferedReader(new FileReader("usuarios.txt"));
                String s;
                boolean si = false;
                String sal = "0.0";
                String tipoLogeado = ""; 

                while ((s = read.readLine()) != null) {
                    String[] arr = s.split(",");
                    if (arr[0].equals(v.txtCedula.getText()) && arr[1].equals(new String(v.txtClave.getPassword()))) {
                        si = true;
                        sal = arr[2]; 
                        tipoLogeado = arr[3]; 
                        break;
                    }
                }
                read.close();
                // Bifurcacion usando A y C

                if (si) {
                    // Caso admin
                    v.dispose(); 
                    if (tipoLogeado.equals("A")) {
                        AdminVista av = new AdminVista();
                        av.setVisible(true);
                        JOptionPane.showMessageDialog(null, "Bienvenido al Panel de Control, Administrador");
                    } else {
                        //Caso comensal
                        ComeUCVView mainV = new ComeUCVView();
                        RepositorioSemana repo = new RepositorioSemana();
                        Monedero mon = new Monedero(v.txtCedula.getText(), Double.parseDouble(sal));
                        
                        ComeUCVController con = new ComeUCVController(mainV, repo, mon);
                        con.init();
                        mainV.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Datos malos o no existes");
                }
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "Error: el archivo no existe o está vacío");
            }
        });
    }
}