/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.management.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Haitam
 */
public class Menu extends javax.swing.JFrame {

    public static MyConnection cnav;
    MyConnection cnav2;

    public Menu() {
        initComponents();
        
        
//        setLayout(new BorderLayout());
//        setContentPane(new JLabel(new ImageIcon("C:\\Users\\Spyweeb\\Desktop\\Icons\\blue-bg-flat-with-triangles.png")));

        this.setLocationRelativeTo(null);
        jTable1.getTableHeader().setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        RefreshEmpList();
        RefreshUsersList();
        firstEmp();
        firstUser();
        date_début_fld.setDate(new Date());
        fillHistoTable();
//        fillIdUtiCombo();
                
        try
        {
               String get_type = "SELECT nom_document FROM document"
                       + " WHERE nom_document IN ('ATT_CONGÉ_MALADIE', 'ATT_CONGÉ_NAISSANCE', 'ATT_CONGÉ_MARIAGE')";
               
               MyConnection c = new MyConnection(false);
               c.MyExecQuery(get_type);
               
               while(c.rs.next())
               {
                   String str = c.rs.getString(1);
                   type_congé_combo.addItem(str.substring(4));
               }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    public void checkDateDernierPaiement()
    {
        try
        {
            Date date_dernier_paiement = null;
            Date date_embauche = null;
            
            String getDateDp = "SELECT date_dernier_paiement FROM attestation_salaire "
                            + " WHERE matricule = "+Integer.parseInt(rechercher_fld_sal.getText());
            
            String getDateEmb = "SELECT date_embauche FROM employé"
                    + " WHERE matricule = "+Integer.parseInt(rechercher_fld_sal.getText());
            
            MyConnection c =  new MyConnection(false);
            c.MyExecQuery(getDateDp);
            if(c.rs.next())
            {
                date_dernier_paiement = c.rs.getDate(1);
            }
            
            MyConnection cc = new MyConnection(false);
            cc.MyExecQuery(getDateEmb);
            if(cc.rs.next())
            {
                date_embauche = cc.rs.getDate(1);
            }
            
                if(date_dernier_paiement == null)
                {
                    date_dpaiement_fld.setDate(date_embauche);
                }
                else
                {
                    date_dpaiement_fld.setDate(date_dernier_paiement);
                }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean checkDuplicateEmp(String matricule) {
        boolean exists = false;
        String sqlCheck = "SELECT matricule FROM employé WHERE matricule = " + matricule;

        try {
            MyConnection c = new MyConnection(false);
            c.MyExecQuery(sqlCheck);
            if (c.rs.next()) {
                exists = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return exists;
    }

    public void firstEmp() {
        try {
            if (cnav.rs.first()) {
                matricule_fld.setText(cnav.rs.getString(1));
                nom_fld.setText(cnav.rs.getString(2));
                prénom_fld.setText(cnav.rs.getString(3));
                emploi_combo.setSelectedItem(cnav.rs.getString(4));
                tél_fld.setText(cnav.rs.getString(5));
                email_fld.setText(cnav.rs.getString(6));
                adresse_fld.setText(cnav.rs.getString(7));
                date_embauche_fld.setDate(cnav.rs.getDate(8));
                date_sortie_fld.setDate(cnav.rs.getDate(9));
                salaire_fld.setText(cnav.rs.getString(10));

            }

            if (cnav.rs.isFirst()) {
                first_btn.setEnabled(false);
                previous_btn.setEnabled(false);
                next_btn.setEnabled(true);
                last_btn.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void RefreshEmpList() {
        try {
            
//            String sql = "SELECT * FROM employé";
//            
//            String type = "";
//            String getType = "SELECT type FROM utilisateur WHERE identifiant = '"+menu_connected_user.getText()+"'";
//            MyConnection c = new MyConnection(false);
//            c.MyExecQuery(getType);
//            if(c.rs.next())
//            {
//                type = c.rs.getString(1);
//                System.out.println("type = "+type);
//            }
            //---------------------------------//
            cnav = new MyConnection(true);
            cnav.MyExecQuery("SELECT * FROM employé WHERE id_utilisateur IS NULL ORDER BY matricule");


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //-----------------------------------//
    public void RefreshUsersList() {
        try {
            cnav2 = new MyConnection(true);
            cnav2.MyExecQuery("SELECT * FROM utilisateur WHERE type <> 'Administrateur' ORDER BY id_utilisateur");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void firstUser() {
        try {
            if (cnav2.rs.first()) {
                id_utilisateur_fld.setText(cnav2.rs.getString(1));
                identifiant_fld_uti.setText(cnav2.rs.getString(2));
                motdepasse_fld_uti.setText(cnav2.rs.getString(3));
                type_utilisateur_fld.setText(cnav2.rs.getString(4));
            }

            if (cnav2.rs.isFirst()) {
                first_btn_uti.setEnabled(false);
                previous_btn_uti.setEnabled(false);
                next_btn_uti.setEnabled(true);
                last_btn_uti.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkDuplicateUser(String id_utilisateur) {
        boolean exists = false;
        String sqlCheck = "SELECT id_utilisateur FROM utilisateur WHERE id_utilisateur = " + id_utilisateur;

        try {
            MyConnection c = new MyConnection(false);
            c.MyExecQuery(sqlCheck);
            if (c.rs.next()) {
                exists = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return exists;
    }
    
    public void fillHistoTable()
    {
        try
        {
           DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
           String fillQuery = "SELECT id_historique, u.identifiant, d.nom_document, e.nom, TO_CHAR(date_impression, 'dd/MM/yyyy HH:MI:SS')"
                            + " FROM utilisateur u, document d, employé e, historique h"
                            + " WHERE h.matricule = e.matricule"
                            + " AND h.id_utilisateur = u.id_utilisateur"
                            + " AND h.id_document = d.id_document"
                            + " ORDER BY id_historique";
           MyConnection c = new MyConnection(false);
           c.MyExecQuery(fillQuery);
           while(c.rs.next())
           {
               dtm.addRow(new Object[]
               {
                   c.rs.getString(1),
                   c.rs.getString(2),
                   c.rs.getString(3),
                   c.rs.getString(4),
                   c.rs.getString(5)
               });
               
                int rc = dtm.getRowCount();
                nombre_lignes_label.setText(Integer.toString(rc));
           }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
//    public void fillIdUtiCombo()
//    {
//        try
//        {
//            MyConnection c = new MyConnection(false);
//            c.MyExecQuery("SELECT id_utilisateur FROM utilisateur ORDER BY id_utilisateur");
//            while(c.rs.next())
//            {
//                id_utilisateur_combo.addItem(c.rs.getString(1));
//            }
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
//    }
    
//    public void connectedUser()
//    {
//        try
//        {
//            String getUserQuery = "SELECT type FROM utilisateur WHERE identifiant = '"+menu_connected_user.getText()+"' ";
//            MyConnection c = new MyConnection(false);
//            c.MyExecQuery(getUserQuery);
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        salariés = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        rechercher_fld = new javax.swing.JTextField();
        rechercher_btn = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        adresse_fld = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        nom_fld = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tél_fld = new javax.swing.JTextField();
        emploi_combo = new javax.swing.JComboBox();
        matricule_fld = new javax.swing.JTextField();
        prénom_fld = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        email_fld = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        salaire_fld = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        date_sortie_fld = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        date_embauche_fld = new com.toedter.calendar.JDateChooser();
        jPanel6 = new javax.swing.JPanel();
        first_btn = new javax.swing.JButton();
        previous_btn = new javax.swing.JButton();
        next_btn = new javax.swing.JButton();
        last_btn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        modifier_btn = new javax.swing.JButton();
        enregistrer_btn = new javax.swing.JButton();
        nouveau_btn = new javax.swing.JButton();
        supprimer_btn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        documents = new javax.swing.JPanel();
        type_document_combo = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        parentPanel = new javax.swing.JPanel();
        salaire = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel8 = new javax.swing.JPanel();
        nom_fld_sal = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        date_paiement_fld = new com.toedter.calendar.JDateChooser();
        prénom_fld_sal = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        salaire_fld_sal = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        date_dpaiement_fld = new com.toedter.calendar.JDateChooser();
        emploi_fld_sal = new javax.swing.JTextField();
        rechercher_btn_sal = new javax.swing.JButton();
        rechercher_fld_sal = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        imprimer_btn_sal = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jLabel29 = new javax.swing.JLabel();
        congé = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanel10 = new javax.swing.JPanel();
        nom_fld_con = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        date_fin_fld = new com.toedter.calendar.JDateChooser();
        prénom_fld_con = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        date_début_fld = new com.toedter.calendar.JDateChooser();
        type_congé_combo = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        emploi_fld_con = new javax.swing.JTextField();
        rechercher_btn_con = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        rechercher_fld_con = new javax.swing.JTextField();
        imprimer_btn_congé = new javax.swing.JButton();
        travail = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        nom_fld_trav = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        prénom_fld_trav = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        date_sortie_trav = new com.toedter.calendar.JDateChooser();
        date_embauche_trav = new com.toedter.calendar.JDateChooser();
        jLabel36 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        emploi_fld_trav = new javax.swing.JTextField();
        rechercher_btn_trav = new javax.swing.JButton();
        rechercher_fld_trav = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        imprimer_btn_trav = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        historique = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel12 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        rechercher_btn_histo = new javax.swing.JButton();
        nombre_lignes_label = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        rechercher_fld_histo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSeparator12 = new javax.swing.JSeparator();
        supprimer_histo_btn = new javax.swing.JButton();
        utilisateurs = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jPanel13 = new javax.swing.JPanel();
        modifier_btn_uti = new javax.swing.JButton();
        enregistrer_btn_uti = new javax.swing.JButton();
        nouveau_btn_uti = new javax.swing.JButton();
        supprimer_btn_uti = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        first_btn_uti = new javax.swing.JButton();
        previous_btn_uti = new javax.swing.JButton();
        last_btn_uti = new javax.swing.JButton();
        next_btn_uti = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel50 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        identifiant_fld_uti = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        type_utilisateur_fld = new javax.swing.JTextField();
        motdepasse_fld_uti = new javax.swing.JTextField();
        id_utilisateur_fld = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        rechercher_btn_uti = new javax.swing.JButton();
        rechercher_fld_uti = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        divers = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        à_propos = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel42 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        manuel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        menu_connected_user = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        mon_compte_btn = new javax.swing.JButton();
        déconnecter_btn = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("e-Document");
        setResizable(false);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N

        salariés.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel3.setText("Recherche par Matricule");

        rechercher_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rechercher_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercher_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rechercher_fldKeyTyped(evt);
            }
        });

        rechercher_btn.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        rechercher_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532752_system-search.png"))); // NOI18N
        rechercher_btn.setText("Rechercher");
        rechercher_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rechercher_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercher_btnActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel2.setText("Matricule");

        jLabel5.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel5.setText("Prénom");

        adresse_fld.setColumns(20);
        adresse_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        adresse_fld.setRows(5);
        adresse_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adresse_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                adresse_fldKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(adresse_fld);

        jLabel4.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel4.setText("Nom");

        nom_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        nom_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nom_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nom_fldKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel6.setText("Téléphone");

        tél_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tél_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tél_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tél_fldKeyTyped(evt);
            }
        });

        emploi_combo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        emploi_combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--", "Formateur", "Comptable", "Manager", "Secrétaire" }));
        emploi_combo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        matricule_fld.setEditable(false);
        matricule_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        matricule_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                matricule_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                matricule_fldKeyTyped(evt);
            }
        });

        prénom_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        prénom_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                prénom_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                prénom_fldKeyTyped(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel7.setText("Email");

        email_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        email_fld.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                email_fldFocusLost(evt);
            }
        });
        email_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                email_fldKeyTyped(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel8.setText("Adresse");

        jLabel10.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel10.setText("Salaire (DH)");

        jLabel22.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498544775_user.png"))); // NOI18N
        jLabel22.setText("Informations de l'employé");

        salaire_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        salaire_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                salaire_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                salaire_fldKeyTyped(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel32.setText("Date embauche");

        jLabel33.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel33.setText("Emploi");

        date_sortie_fld.setDateFormatString("dd/MM/yyyy");
        date_sortie_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel34.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel34.setText("Date sortie");

        date_embauche_fld.setDateFormatString("dd/MM/yyyy");
        date_embauche_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel8)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel7))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(email_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tél_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addGap(38, 38, 38))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(matricule_fld)
                                            .addComponent(prénom_fld)
                                            .addComponent(nom_fld, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(108, 108, 108)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                        .addComponent(salaire_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(emploi_combo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel33)
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel32)
                                                    .addComponent(jLabel34))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(date_sortie_fld, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                                    .addComponent(date_embauche_fld, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(0, 60, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(salaire_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(matricule_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(emploi_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nom_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prénom_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tél_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(email_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(date_embauche_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(date_sortie_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69))
        );

        first_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117657_go-first.png"))); // NOI18N
        first_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        first_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                first_btnActionPerformed(evt);
            }
        });

        previous_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117572_go-previous.png"))); // NOI18N
        previous_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        previous_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previous_btnActionPerformed(evt);
            }
        });

        next_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117691_go-next.png"))); // NOI18N
        next_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        next_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next_btnActionPerformed(evt);
            }
        });

        last_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117726_go-last.png"))); // NOI18N
        last_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        last_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                last_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(first_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previous_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(next_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(last_btn)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(first_btn)
                    .addComponent(previous_btn)
                    .addComponent(next_btn)
                    .addComponent(last_btn))
                .addContainerGap())
        );

        modifier_btn.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        modifier_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118390_pencil_16.png"))); // NOI18N
        modifier_btn.setText("Modifier");
        modifier_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modifier_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifier_btnActionPerformed(evt);
            }
        });

        enregistrer_btn.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        enregistrer_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1493076878_save.png"))); // NOI18N
        enregistrer_btn.setText("Enregistrer");
        enregistrer_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        enregistrer_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enregistrer_btnActionPerformed(evt);
            }
        });

        nouveau_btn.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        nouveau_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118358_plus_16.png"))); // NOI18N
        nouveau_btn.setText("Nouveau");
        nouveau_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        nouveau_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nouveau_btnActionPerformed(evt);
            }
        });

        supprimer_btn.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        supprimer_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118409_delete_16.png"))); // NOI18N
        supprimer_btn.setText("Supprimer");
        supprimer_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        supprimer_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimer_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nouveau_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(enregistrer_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(modifier_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(supprimer_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nouveau_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enregistrer_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(modifier_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(supprimer_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel14.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        jLabel14.setText("Gestion des salariés");

        javax.swing.GroupLayout salariésLayout = new javax.swing.GroupLayout(salariés);
        salariés.setLayout(salariésLayout);
        salariésLayout.setHorizontalGroup(
            salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salariésLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salariésLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addGroup(salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(salariésLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rechercher_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rechercher_btn))
                            .addGroup(salariésLayout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(salariésLayout.createSequentialGroup()
                        .addGroup(salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        salariésLayout.setVerticalGroup(
            salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salariésLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rechercher_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rechercher_btn)
                    .addComponent(jLabel3))
                .addGroup(salariésLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(salariésLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(salariésLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Salariés", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498531689_toolbox.png")), salariés); // NOI18N

        type_document_combo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        type_document_combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ATT_SALAIRE", "ATT_TRAVAIL", "ATT_CONGÉ" }));
        type_document_combo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        type_document_combo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                type_document_comboItemStateChanged(evt);
            }
        });
        type_document_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_document_comboActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel13.setText("Type de document");

        jLabel15.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        jLabel15.setText("Gestion des documents administratifs");

        parentPanel.setLayout(new java.awt.CardLayout());

        salaire.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        nom_fld_sal.setEditable(false);
        nom_fld_sal.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel28.setText("Salaire (DH)");

        jLabel27.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel27.setText("Emploi");

        jLabel26.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel26.setText("Prénom");

        jLabel35.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel35.setText("Date paiement");

        date_paiement_fld.setDateFormatString("dd/MM/yyyy");
        date_paiement_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        prénom_fld_sal.setEditable(false);
        prénom_fld_sal.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel30.setText("Date dernier paiement");

        salaire_fld_sal.setEditable(false);
        salaire_fld_sal.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel21.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel21.setText("Nom");

        date_dpaiement_fld.setDateFormatString("dd/MM/yyyy");
        date_dpaiement_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        emploi_fld_sal.setEditable(false);
        emploi_fld_sal.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(prénom_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nom_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(date_dpaiement_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(63, 63, 63)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel35)))
                            .addComponent(jLabel21))
                        .addGap(51, 51, 51)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(date_paiement_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(emploi_fld_sal, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(salaire_fld_sal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(nom_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(emploi_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(prénom_fld_sal)
                    .addComponent(jLabel28)
                    .addComponent(salaire_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(date_dpaiement_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30))
                    .addComponent(date_paiement_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addContainerGap())
        );

        rechercher_btn_sal.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        rechercher_btn_sal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532752_system-search.png"))); // NOI18N
        rechercher_btn_sal.setText("Rechercher");
        rechercher_btn_sal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rechercher_btn_sal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercher_btn_salActionPerformed(evt);
            }
        });

        rechercher_fld_sal.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rechercher_fld_sal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercher_fld_salKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rechercher_fld_salKeyTyped(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel19.setText("Recherche par Matricule");

        imprimer_btn_sal.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        imprimer_btn_sal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1493154109_Print.png"))); // NOI18N
        imprimer_btn_sal.setText("Imprimer l'Attestation");
        imprimer_btn_sal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imprimer_btn_sal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimer_btn_salActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498544775_user.png"))); // NOI18N
        jLabel29.setText("Informations de l'employé(e)");
        jScrollPane4.setViewportView(jLabel29);

        javax.swing.GroupLayout salaireLayout = new javax.swing.GroupLayout(salaire);
        salaire.setLayout(salaireLayout);
        salaireLayout.setHorizontalGroup(
            salaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salaireLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(imprimer_btn_sal, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(235, 235, 235))
            .addGroup(salaireLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(salaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(salaireLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rechercher_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rechercher_btn_sal))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        salaireLayout.setVerticalGroup(
            salaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salaireLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(salaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rechercher_fld_sal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rechercher_btn_sal)
                    .addComponent(jLabel19))
                .addGap(29, 29, 29)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(59, 59, 59)
                .addComponent(imprimer_btn_sal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );

        parentPanel.add(salaire, "card3");

        congé.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel40.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498544775_user.png"))); // NOI18N
        jLabel40.setText("Informations de l'employé(e)");

        nom_fld_con.setEditable(false);
        nom_fld_con.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel43.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel43.setText("Emploi");

        jLabel44.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel44.setText("Prénom");

        jLabel45.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel45.setText("Date fin");

        date_fin_fld.setDateFormatString("dd/MM/yyyy");
        date_fin_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        prénom_fld_con.setEditable(false);
        prénom_fld_con.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel46.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel46.setText("Date début");

        jLabel23.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel23.setText("Nom");

        date_début_fld.setDateFormatString("dd/MM/yyyy");
        date_début_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        type_congé_combo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        type_congé_combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--" }));
        type_congé_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_congé_comboActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel47.setText("Type congé");

        emploi_fld_con.setEditable(false);
        emploi_fld_con.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(prénom_fld_con, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nom_fld_con, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(date_début_fld, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(44, 44, 44)
                        .addComponent(date_fin_fld, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addGap(50, 50, 50)))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(type_congé_combo, 0, 169, Short.MAX_VALUE)
                            .addComponent(emploi_fld_con))))
                .addGap(36, 36, 36))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel43)
                            .addComponent(emploi_fld_con, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(type_congé_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(date_fin_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45))
                        .addContainerGap())
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(nom_fld_con, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44)
                            .addComponent(prénom_fld_con))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(date_début_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46))
                        .addGap(11, 11, 11))))
        );

        rechercher_btn_con.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        rechercher_btn_con.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532752_system-search.png"))); // NOI18N
        rechercher_btn_con.setText("Rechercher");
        rechercher_btn_con.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rechercher_btn_con.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercher_btn_conActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel18.setText("Recherche par Matricule");

        rechercher_fld_con.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rechercher_fld_con.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercher_fld_conKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rechercher_fld_conKeyTyped(evt);
            }
        });

        imprimer_btn_congé.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        imprimer_btn_congé.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1493154109_Print.png"))); // NOI18N
        imprimer_btn_congé.setText("Imprimer l'Attestation");
        imprimer_btn_congé.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imprimer_btn_congé.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimer_btn_congéActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout congéLayout = new javax.swing.GroupLayout(congé);
        congé.setLayout(congéLayout);
        congéLayout.setHorizontalGroup(
            congéLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(congéLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(congéLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(congéLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rechercher_fld_con, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rechercher_btn_con))
                    .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(52, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, congéLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(imprimer_btn_congé, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(234, 234, 234))
        );
        congéLayout.setVerticalGroup(
            congéLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, congéLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addGap(4, 4, 4)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(congéLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rechercher_fld_con, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rechercher_btn_con)
                    .addComponent(jLabel18))
                .addGap(32, 32, 32)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(imprimer_btn_congé, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        parentPanel.add(congé, "card2");

        travail.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel31.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498544775_user.png"))); // NOI18N
        jLabel31.setText("Informations de l'employé");

        nom_fld_trav.setEditable(false);
        nom_fld_trav.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel37.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel37.setText("Emploi");

        jLabel38.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel38.setText("Prénom");

        prénom_fld_trav.setEditable(false);
        prénom_fld_trav.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel41.setText("Nom");

        date_sortie_trav.setDateFormatString("dd/MM/yyyy");
        date_sortie_trav.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        date_embauche_trav.setDateFormatString("dd/MM/yyyy");
        date_embauche_trav.setEnabled(false);
        date_embauche_trav.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel36.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel36.setText("Date embauche");

        jLabel39.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel39.setText("Date sortie");

        emploi_fld_trav.setEditable(false);
        emploi_fld_trav.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37))
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(prénom_fld_trav)
                    .addComponent(nom_fld_trav)
                    .addComponent(emploi_fld_trav, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addComponent(jLabel39))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(date_sortie_trav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(date_embauche_trav, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(nom_fld_trav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(prénom_fld_trav))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(emploi_fld_trav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_embauche_trav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addComponent(date_sortie_trav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rechercher_btn_trav.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        rechercher_btn_trav.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532752_system-search.png"))); // NOI18N
        rechercher_btn_trav.setText("Rechercher");
        rechercher_btn_trav.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rechercher_btn_trav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercher_btn_travActionPerformed(evt);
            }
        });

        rechercher_fld_trav.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rechercher_fld_trav.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercher_fld_travKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rechercher_fld_travKeyTyped(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel17.setText("Recherche par Matricule");

        imprimer_btn_trav.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        imprimer_btn_trav.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1493154109_Print.png"))); // NOI18N
        imprimer_btn_trav.setText("Imprimer l'Attestation");
        imprimer_btn_trav.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imprimer_btn_trav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimer_btn_travActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout travailLayout = new javax.swing.GroupLayout(travail);
        travail.setLayout(travailLayout);
        travailLayout.setHorizontalGroup(
            travailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(travailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(travailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(travailLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rechercher_fld_trav, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rechercher_btn_trav))
                    .addGroup(travailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(101, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, travailLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(imprimer_btn_trav, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(231, 231, 231))
        );
        travailLayout.setVerticalGroup(
            travailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, travailLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(travailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rechercher_fld_trav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rechercher_btn_trav)
                    .addComponent(jLabel17))
                .addGap(31, 31, 31)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(49, 49, 49)
                .addComponent(imprimer_btn_trav, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        parentPanel.add(travail, "card4");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 63, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout documentsLayout = new javax.swing.GroupLayout(documents);
        documents.setLayout(documentsLayout);
        documentsLayout.setHorizontalGroup(
            documentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(documentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2))
                .addContainerGap(568, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, documentsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(documentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(documentsLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(type_document_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(parentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(78, 78, 78))
        );
        documentsLayout.setVerticalGroup(
            documentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(documentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(type_document_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(parentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Documents", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532197_clipboard.png")), documents); // NOI18N

        jLabel16.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        jLabel16.setText("Historique des impressions");

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel11.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel11.setText("Recherche par Matricule");

        rechercher_btn_histo.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        rechercher_btn_histo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532752_system-search.png"))); // NOI18N
        rechercher_btn_histo.setText("Rechercher");
        rechercher_btn_histo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rechercher_btn_histo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercher_btn_histoActionPerformed(evt);
            }
        });

        nombre_lignes_label.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        nombre_lignes_label.setText("0");

        jLabel12.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel12.setText("Nombre de lignes :");

        rechercher_fld_histo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rechercher_fld_histo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercher_fld_histoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rechercher_fld_histoKeyTyped(evt);
            }
        });

        jTable1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID historique", "Utilisateur", "Nom document", "Employé", "Date impression"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 780, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rechercher_fld_histo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rechercher_btn_histo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator12)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nombre_lignes_label)
                .addGap(36, 36, 36))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rechercher_fld_histo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rechercher_btn_histo)
                    .addComponent(jLabel11))
                .addGap(26, 26, 26)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(nombre_lignes_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        supprimer_histo_btn.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        supprimer_histo_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118409_delete_16.png"))); // NOI18N
        supprimer_histo_btn.setText("Supprimer l'historique selectionnée");
        supprimer_histo_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        supprimer_histo_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimer_histo_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout historiqueLayout = new javax.swing.GroupLayout(historique);
        historique.setLayout(historiqueLayout);
        historiqueLayout.setHorizontalGroup(
            historiqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historiqueLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(historiqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(historiqueLayout.createSequentialGroup()
                        .addGroup(historiqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(568, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, historiqueLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(historiqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(supprimer_histo_btn)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))))
        );
        historiqueLayout.setVerticalGroup(
            historiqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historiqueLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(supprimer_histo_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Historique", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498538972_bookshelf.png")), historique); // NOI18N

        jLabel20.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        jLabel20.setText("Gestion des utilisateurs");

        modifier_btn_uti.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        modifier_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118390_pencil_16.png"))); // NOI18N
        modifier_btn_uti.setText("Modifier");
        modifier_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modifier_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifier_btn_utiActionPerformed(evt);
            }
        });

        enregistrer_btn_uti.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        enregistrer_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1493076878_save.png"))); // NOI18N
        enregistrer_btn_uti.setText("Enregistrer");
        enregistrer_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        enregistrer_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enregistrer_btn_utiActionPerformed(evt);
            }
        });

        nouveau_btn_uti.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        nouveau_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118358_plus_16.png"))); // NOI18N
        nouveau_btn_uti.setText("Nouveau");
        nouveau_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        nouveau_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nouveau_btn_utiActionPerformed(evt);
            }
        });

        supprimer_btn_uti.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        supprimer_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492118409_delete_16.png"))); // NOI18N
        supprimer_btn_uti.setText("Supprimer");
        supprimer_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        supprimer_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimer_btn_utiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nouveau_btn_uti, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(enregistrer_btn_uti, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(modifier_btn_uti, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(supprimer_btn_uti, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(nouveau_btn_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(enregistrer_btn_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(modifier_btn_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(supprimer_btn_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        first_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117657_go-first.png"))); // NOI18N
        first_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        first_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                first_btn_utiActionPerformed(evt);
            }
        });

        previous_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117572_go-previous.png"))); // NOI18N
        previous_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        previous_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previous_btn_utiActionPerformed(evt);
            }
        });

        last_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117726_go-last.png"))); // NOI18N
        last_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        last_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                last_btn_utiActionPerformed(evt);
            }
        });

        next_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1492117691_go-next.png"))); // NOI18N
        next_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        next_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next_btn_utiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(first_btn_uti)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previous_btn_uti)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(next_btn_uti)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(last_btn_uti)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(first_btn_uti)
                    .addComponent(previous_btn_uti)
                    .addComponent(next_btn_uti)
                    .addComponent(last_btn_uti))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel54.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel54.setText("Identifiant ");

        jLabel50.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLabel50.setText("Informations d'utilisateur");

        jLabel53.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel53.setText("Mot de passe ");

        identifiant_fld_uti.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        identifiant_fld_uti.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                identifiant_fld_utiKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                identifiant_fld_utiKeyTyped(evt);
            }
        });

        jLabel55.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel55.setText("Type d'utilisateur");

        type_utilisateur_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        type_utilisateur_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                type_utilisateur_fldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                type_utilisateur_fldKeyTyped(evt);
            }
        });

        motdepasse_fld_uti.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        motdepasse_fld_uti.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                motdepasse_fld_utiKeyTyped(evt);
            }
        });

        id_utilisateur_fld.setEditable(false);
        id_utilisateur_fld.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        id_utilisateur_fld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                id_utilisateur_fldKeyTyped(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel56.setText("ID utilisateur");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53)
                            .addComponent(jLabel54))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(identifiant_fld_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(motdepasse_fld_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55)
                            .addComponent(jLabel56))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(type_utilisateur_fld)
                            .addComponent(id_utilisateur_fld)))
                    .addComponent(jSeparator11)
                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50)
                .addGap(4, 4, 4)
                .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(id_utilisateur_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(type_utilisateur_fld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(identifiant_fld_uti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(motdepasse_fld_uti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        rechercher_btn_uti.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        rechercher_btn_uti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498532752_system-search.png"))); // NOI18N
        rechercher_btn_uti.setText("Rechercher");
        rechercher_btn_uti.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rechercher_btn_uti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercher_btn_utiActionPerformed(evt);
            }
        });

        rechercher_fld_uti.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rechercher_fld_uti.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercher_fld_utiKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rechercher_fld_utiKeyTyped(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel24.setText("Recherche par ID utilisateur");

        javax.swing.GroupLayout utilisateursLayout = new javax.swing.GroupLayout(utilisateurs);
        utilisateurs.setLayout(utilisateursLayout);
        utilisateursLayout.setHorizontalGroup(
            utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, utilisateursLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, utilisateursLayout.createSequentialGroup()
                        .addGroup(utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(568, 568, 568))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, utilisateursLayout.createSequentialGroup()
                        .addGroup(utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(utilisateursLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(rechercher_fld_uti, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rechercher_btn_uti))
                            .addGroup(utilisateursLayout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(188, 188, 188))))
            .addGroup(utilisateursLayout.createSequentialGroup()
                .addGap(256, 256, 256)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        utilisateursLayout.setVerticalGroup(
            utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(utilisateursLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rechercher_fld_uti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rechercher_btn_uti)
                    .addComponent(jLabel24))
                .addGap(25, 25, 25)
                .addGroup(utilisateursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Utilisateurs", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498527798_running.png")), utilisateurs); // NOI18N

        jLabel25.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 18)); // NOI18N
        jLabel25.setText("Divers");

        jTabbedPane2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane2.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N

        à_propos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, \nsed do eiusmod tempor incididunt ut labore et dolore magna aliqua. \nUt enim ad minim veniam, quis nostrud exercitation ullamco,\nlaboris nisi ut aliquip ex ea commodo consequat. Duis aute irure,\ndolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat,\nnulla pariatur.Excepteur sint occaecat cupidatat non proident, \nsunt in culpa qui officia deserunt mollit anim id est laborum.");
        jTextArea2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setViewportView(jTextArea2);

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/OFPPT-Logo-2.png"))); // NOI18N

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498531187_document.png"))); // NOI18N

        jLabel49.setFont(new java.awt.Font("Lucida Sans", 0, 18)); // NOI18N
        jLabel49.setText("e-Document");

        jLabel51.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        jLabel51.setText("Version 1.0 créé par");

        jLabel52.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        jLabel52.setText("HAMDAN Haitam.");

        javax.swing.GroupLayout à_proposLayout = new javax.swing.GroupLayout(à_propos);
        à_propos.setLayout(à_proposLayout);
        à_proposLayout.setHorizontalGroup(
            à_proposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(à_proposLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(à_proposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(à_proposLayout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel42))
                    .addGroup(à_proposLayout.createSequentialGroup()
                        .addGroup(à_proposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel52)
                            .addComponent(jLabel51)
                            .addComponent(jLabel49)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 8, Short.MAX_VALUE)))
                .addContainerGap())
        );
        à_proposLayout.setVerticalGroup(
            à_proposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, à_proposLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(à_proposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("À propos", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498583368_Information.png")), à_propos); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton1.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498584336_pdf.png"))); // NOI18N
        jButton1.setText("Afficher PDF");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout manuelLayout = new javax.swing.GroupLayout(manuel);
        manuel.setLayout(manuelLayout);
        manuelLayout.setHorizontalGroup(
            manuelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manuelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(282, Short.MAX_VALUE))
        );
        manuelLayout.setVerticalGroup(
            manuelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manuelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Manuel d'utilisation", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498583841_Help.png")), manuel); // NOI18N

        javax.swing.GroupLayout diversLayout = new javax.swing.GroupLayout(divers);
        divers.setLayout(diversLayout);
        diversLayout.setHorizontalGroup(
            diversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diversLayout.createSequentialGroup()
                .addGroup(diversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(diversLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(diversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(diversLayout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 710, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        diversLayout.setVerticalGroup(
            diversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diversLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(182, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Divers", new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498531033_gear.png")), divers); // NOI18N

        menu_connected_user.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        menu_connected_user.setForeground(new java.awt.Color(0, 204, 0));
        menu_connected_user.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498530993_profle.png"))); // NOI18N
        menu_connected_user.setText("jLabel1");

        jLabel1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLabel1.setText("Bienvenue");

        mon_compte_btn.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        mon_compte_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498582507_tools.png"))); // NOI18N
        mon_compte_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mon_compte_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mon_compte_btnActionPerformed(evt);
            }
        });

        déconnecter_btn.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        déconnecter_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/1498530814_power.png"))); // NOI18N
        déconnecter_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        déconnecter_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                déconnecter_btnActionPerformed(evt);
            }
        });

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hr/management/app/OFPPT-Logo-2.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 913, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(menu_connected_user))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(mon_compte_btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(déconnecter_btn)))
                        .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel9)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(menu_connected_user)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mon_compte_btn)
                            .addComponent(déconnecter_btn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 521, Short.MAX_VALUE)
                        .addComponent(jLabel9))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void déconnecter_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_déconnecter_btnActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment déconnecter ?");
        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};
        try {
            int response = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (response == JOptionPane.YES_OPTION) {
                this.setVisible(false);
                new Login().setVisible(true);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_déconnecter_btnActionPerformed

    private void type_document_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_document_comboActionPerformed

        if (type_document_combo.getSelectedIndex() == 0) {
            parentPanel.removeAll();
            parentPanel.add(salaire);
            parentPanel.repaint();
            parentPanel.revalidate();
        } else if (type_document_combo.getSelectedIndex() == 1) {
            parentPanel.removeAll();
            parentPanel.add(travail);
            parentPanel.repaint();
            parentPanel.revalidate();
        } else if (type_document_combo.getSelectedIndex() == 2) {
            parentPanel.removeAll();
            parentPanel.add(congé);
            parentPanel.repaint();
            parentPanel.revalidate();
        }
    }//GEN-LAST:event_type_document_comboActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File("C:\\Users\\Spyweeb\\Desktop\\Projet Fin Formation\\manuel_edocument.pdf");
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void mon_compte_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mon_compte_btnActionPerformed

        Mon_compte mc = new Mon_compte();
        mc.identifiant_fld.setText(menu_connected_user.getText());
        String getPass = "SELECT mot_de_passe FROM utilisateur WHERE identifiant = '" + mc.identifiant_fld.getText() + "'";

        try {
            MyConnection c = new MyConnection(false);
            c.MyExecQuery(getPass);
            if (c.rs.next()) {
                mc.motdepasse_fld.setText(c.rs.getString(1));
            }
            mc.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_mon_compte_btnActionPerformed

    private void rechercher_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercher_btnActionPerformed

        JLabel message = new JLabel("Tous les champs sont obligatoires !");
        JLabel message2 = new JLabel("Employé non trouvé !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!rechercher_fld.getText().trim().isEmpty()) {
            String searchQuery = "SELECT * FROM employé WHERE id_utilisateur IS NULL AND matricule = " + rechercher_fld.getText();
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecQuery(searchQuery);

                if (c.rs.next()) {
                    matricule_fld.setText(c.rs.getString(1));
                    nom_fld.setText(c.rs.getString(2));
                    prénom_fld.setText(c.rs.getString(3));
                    emploi_combo.setSelectedItem(c.rs.getString(4));
                    tél_fld.setText(c.rs.getString(5));
                    email_fld.setText(c.rs.getString(6));
                    adresse_fld.setText(c.rs.getString(7));
                    date_embauche_fld.setDate(c.rs.getDate(8));
                    date_sortie_fld.setDate(c.rs.getDate(9));
                    salaire_fld.setText(c.rs.getString(10));
                } else {
                    JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.ERROR_MESSAGE);
                    rechercher_fld.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_rechercher_btnActionPerformed

    private void first_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_first_btnActionPerformed

        firstEmp();
    }//GEN-LAST:event_first_btnActionPerformed

    private void previous_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previous_btnActionPerformed

        try {
            if (cnav.rs.previous()) {
                matricule_fld.setText(cnav.rs.getString(1));
                nom_fld.setText(cnav.rs.getString(2));
                prénom_fld.setText(cnav.rs.getString(3));
                emploi_combo.setSelectedItem(cnav.rs.getString(4));
                tél_fld.setText(cnav.rs.getString(5));
                email_fld.setText(cnav.rs.getString(6));
                adresse_fld.setText(cnav.rs.getString(7));
                date_embauche_fld.setDate(cnav.rs.getDate(8));
                date_sortie_fld.setDate(cnav.rs.getDate(9));
                salaire_fld.setText(cnav.rs.getString(10));   

                next_btn.setEnabled(true);
                last_btn.setEnabled(true);
            }

            if (cnav.rs.isFirst()) {
                first_btn.setEnabled(false);
                previous_btn.setEnabled(false);
                next_btn.setEnabled(true);
                last_btn.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_previous_btnActionPerformed

    private void next_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next_btnActionPerformed

        try {
            if (cnav.rs.next()) {
                matricule_fld.setText(cnav.rs.getString(1));
                nom_fld.setText(cnav.rs.getString(2));
                prénom_fld.setText(cnav.rs.getString(3));
                emploi_combo.setSelectedItem(cnav.rs.getString(4));
                tél_fld.setText(cnav.rs.getString(5));
                email_fld.setText(cnav.rs.getString(6));
                adresse_fld.setText(cnav.rs.getString(7));
                date_embauche_fld.setDate(cnav.rs.getDate(8));
                date_sortie_fld.setDate(cnav.rs.getDate(9));
                salaire_fld.setText(cnav.rs.getString(10));

                first_btn.setEnabled(true);
                previous_btn.setEnabled(true);
            }

            if (cnav.rs.isLast()) {
                next_btn.setEnabled(false);
                last_btn.setEnabled(false);
                first_btn.setEnabled(true);
                previous_btn.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_next_btnActionPerformed

    private void last_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_last_btnActionPerformed

        try {
            if (cnav.rs.last()) {
                matricule_fld.setText(cnav.rs.getString(1));
                nom_fld.setText(cnav.rs.getString(2));
                prénom_fld.setText(cnav.rs.getString(3));
                emploi_combo.setSelectedItem(cnav.rs.getString(4));
                tél_fld.setText(cnav.rs.getString(5));
                email_fld.setText(cnav.rs.getString(6));
                adresse_fld.setText(cnav.rs.getString(7));
                date_embauche_fld.setDate(cnav.rs.getDate(8));
                date_sortie_fld.setDate(cnav.rs.getDate(9));
                salaire_fld.setText(cnav.rs.getString(10));

            }

            if (cnav.rs.isLast()) {
                last_btn.setEnabled(false);
                next_btn.setEnabled(false);
                previous_btn.setEnabled(true);
                first_btn.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_last_btnActionPerformed

    private void nouveau_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nouveau_btnActionPerformed

        try {
            MyConnection c = new MyConnection(false);
            c.MyExecQuery("SELECT NVL(MAX(matricule)+1,1) FROM employé");
            if (c.rs.next()) {
                matricule_fld.setText(c.rs.getString(1));
            }
            rechercher_fld.setText("");
            nom_fld.setText("");
            prénom_fld.setText("");
            tél_fld.setText("");
            email_fld.setText("");
            adresse_fld.setText("");
            emploi_combo.setSelectedIndex(0);
            salaire_fld.setText("");
            date_embauche_fld.setDate(null);
            date_sortie_fld.setDate(null);
            

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_nouveau_btnActionPerformed

    private void enregistrer_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enregistrer_btnActionPerformed

        JLabel message = new JLabel("Un employé existe déjà avec cette matricule !");
        JLabel message2 = new JLabel("Employé ajouté avec succès !");
        JLabel message3 = new JLabel("Erreur lors de l'ajout de cet employé !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!matricule_fld.getText().trim().isEmpty() && !nom_fld.getText().trim().isEmpty()
                && !prénom_fld.getText().trim().isEmpty() && !tél_fld.getText().trim().isEmpty()
                && !email_fld.getText().trim().isEmpty() && !adresse_fld.getText().trim().isEmpty()
                && emploi_combo.getSelectedIndex() != 0 && !salaire_fld.getText().trim().isEmpty()
                && date_embauche_fld.getDate() != null) {
            try {
                int matricule = Integer.parseInt(matricule_fld.getText());
                String nom = nom_fld.getText();
                String prénom = prénom_fld.getText();
                String emploi = emploi_combo.getSelectedItem().toString();
                int tél = Integer.parseInt(tél_fld.getText());
                String email = email_fld.getText();
                String adresse = adresse_fld.getText();
                int salaire = Integer.parseInt(salaire_fld.getText());

                String insertQuery = "INSERT INTO employé(matricule, nom, prénom, emploi, tél, email, adresse, date_embauche, date_sortie, salaire)"
                        + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                MyConnection c = new MyConnection(false);
                c.MyExecPrepStat(insertQuery);
                c.pst.setInt(1, matricule);
                c.pst.setString(2, nom);
                c.pst.setString(3, prénom);
                c.pst.setString(4, emploi);
                c.pst.setInt(5, tél);
                c.pst.setString(6, email);
                c.pst.setString(7, adresse);
                c.pst.setDate(8, new java.sql.Date(date_embauche_fld.getDate().getTime()));
                c.pst.setInt(10, salaire);

                if (date_sortie_fld.getDate() != null) {
                    c.pst.setDate(9, new java.sql.Date(date_sortie_fld.getDate().getTime()));
                } else {
                    c.pst.setString(9, null);
                }

                if (checkDuplicateEmp(matricule_fld.getText())) {
                    JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (c.pst.executeUpdate() == 1) {
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        RefreshEmpList();
                        firstEmp();
                    } else {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_enregistrer_btnActionPerformed

    private void supprimer_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimer_btnActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment supprimer cet employé ?");
        JLabel message2 = new JLabel("Employé supprimé avec succès !");
        JLabel message3 = new JLabel("Erreur lors de la suppression de cet employé !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};

        if (!matricule_fld.getText().trim().isEmpty() && !nom_fld.getText().trim().isEmpty()
                && !prénom_fld.getText().trim().isEmpty() && !tél_fld.getText().trim().isEmpty()
                && !email_fld.getText().trim().isEmpty() && !adresse_fld.getText().trim().isEmpty()
                && emploi_combo.getSelectedIndex() != 0 && !salaire_fld.getText().trim().isEmpty()
                && date_embauche_fld.getDate() != null) {
            String deleteQuery = "DELETE FROM employé WHERE matricule = " + Integer.parseInt(matricule_fld.getText());
//            boolean deleted = false;
            try {
                MyConnection c = new MyConnection(false);
                int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    int count = c.MyExecUpdate(deleteQuery);

//                    deleted = true;
                    if (count == 0) {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        RefreshEmpList();
                        firstEmp(); 
                    }
                }

//                if (deleted == true) {
//                    JOptionPane.showMessageDialog(this, "Employé(e) supprimé avec succès !", "Message", JOptionPane.INFORMATION_MESSAGE);
//                    firstEmp();
//                    RefreshEmpList();
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_supprimer_btnActionPerformed

    private void modifier_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifier_btnActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment modifier cet employé ?");
        JLabel message2 = new JLabel("Employé modifié avec succès !");
        JLabel message3 = new JLabel("Erreur lors de la modification de cet employé !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};

        if (!matricule_fld.getText().trim().isEmpty() && !nom_fld.getText().trim().isEmpty()
                && !prénom_fld.getText().trim().isEmpty() && !tél_fld.getText().trim().isEmpty()
                && !email_fld.getText().trim().isEmpty() && !adresse_fld.getText().trim().isEmpty()
                && emploi_combo.getSelectedIndex() != 0 && !salaire_fld.getText().trim().isEmpty()
                && date_embauche_fld.getDate() != null) {
            String updateQuery = "UPDATE employé SET nom = ?, prénom = ?, emploi = ?, tél = ?, email = ?, adresse = ?, date_embauche = ?,"
                    + " date_sortie = ?, salaire = ? WHERE matricule = ?";
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecPrepStat(updateQuery);
                c.pst.setString(1, nom_fld.getText());
                c.pst.setString(2, prénom_fld.getText());
                c.pst.setString(3, emploi_combo.getSelectedItem().toString());
                c.pst.setInt(4, Integer.parseInt(tél_fld.getText()));
                c.pst.setString(5, email_fld.getText());
                c.pst.setString(6, adresse_fld.getText());
                c.pst.setDate(7, new java.sql.Date(date_embauche_fld.getDate().getTime()));
                c.pst.setInt(9, Integer.parseInt(salaire_fld.getText()));
                c.pst.setInt(10, Integer.parseInt(matricule_fld.getText()));
                if (date_sortie_fld.getDate() != null) {
                    c.pst.setDate(8, new java.sql.Date(date_sortie_fld.getDate().getTime()));
                } else {
                    c.pst.setDate(8, null);
                }

//                boolean updated = false;
                int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    int count = c.pst.executeUpdate();

//                    updated = true;
                    if (count == 0) {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        RefreshEmpList();
                        firstEmp();
                    }
                }

//                if (updated == true) {
//                    JOptionPane.showMessageDialog(this, "Employé(e) modifié avec succès !", "e-Document", JOptionPane.INFORMATION_MESSAGE);
//                    firstEmp();
//                    RefreshEmpList();
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_modifier_btnActionPerformed

    private void matricule_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_matricule_fldKeyTyped

        if (matricule_fld.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_matricule_fldKeyTyped

    private void nom_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nom_fldKeyTyped

        if (nom_fld.getText().trim().length() >= 10) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isDigit(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_nom_fldKeyTyped

    private void prénom_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prénom_fldKeyTyped

        if (prénom_fld.getText().trim().length() >= 10) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isDigit(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_prénom_fldKeyTyped

    private void tél_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tél_fldKeyTyped

        if (tél_fld.getText().trim().length() >= 10) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }

    }//GEN-LAST:event_tél_fldKeyTyped

    private void email_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_email_fldKeyTyped

        if (email_fld.getText().trim().length() >= 25) {
            evt.consume();
        }
    }//GEN-LAST:event_email_fldKeyTyped

    private void adresse_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adresse_fldKeyTyped

        if (adresse_fld.getText().trim().length() >= 25) {
            evt.consume();
        }
    }//GEN-LAST:event_adresse_fldKeyTyped

    private void salaire_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salaire_fldKeyTyped

        if (salaire_fld.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_salaire_fldKeyTyped

    private void matricule_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_matricule_fldKeyReleased

        JLabel message = new JLabel("Matricule doit être numérique !");
        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (matricule_fld.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }

        try {
            String matricule = matricule_fld.getText();
            boolean incorrect = true;

            while (incorrect) {
                Integer.parseInt(matricule);
                incorrect = false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
            matricule_fld.setText("");
        }
    }//GEN-LAST:event_matricule_fldKeyReleased

    private void tél_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tél_fldKeyReleased

        if (!tél_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Téléphone doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
//
//            try {
//                String tél = tél_fld.getText();
//                boolean incorrect = true;
//
//                while (incorrect) {
//                    Integer.parseInt(tél);
//                    incorrect = false;
//                }
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
//                tél_fld.setText("");
//            }
            if (!tél_fld.getText().matches("^[0-9]+$")) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                tél_fld.setText("");
            }
        }
    }//GEN-LAST:event_tél_fldKeyReleased

    private void salaire_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salaire_fldKeyReleased

        if (!salaire_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Salaire doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            try {
                String salaire = salaire_fld.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(salaire);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                salaire_fld.setText("");
            }
        }
    }//GEN-LAST:event_salaire_fldKeyReleased

    private void email_fldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_email_fldFocusLost

        if (!email_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("La syntaxe de cette adresse mail est incorrecte !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            String regex = "^[a-zA-z0-9._-]+@[a-z]+\\.[a-z]{2,3}$";
            String email = email_fld.getText();
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(email);

            if (m.find() == false) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                email_fld.setText("");
            }
        }
    }//GEN-LAST:event_email_fldFocusLost

    private void rechercher_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fldKeyTyped

        if (rechercher_fld.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_rechercher_fldKeyTyped

    private void rechercher_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fldKeyReleased

        if (!rechercher_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Matricule doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (rechercher_fld.getText().trim().length() >= 5) {
                evt.consume();
            }

            char c = evt.getKeyChar();
            if (Character.isLetter(c) && !evt.isAltDown()) {
                evt.consume();
            }

            try {
                String matricule = rechercher_fld.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(matricule);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                rechercher_fld.setText("");
            }
        }
    }//GEN-LAST:event_rechercher_fldKeyReleased

    private void rechercher_btn_salActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercher_btn_salActionPerformed

        JLabel message = new JLabel("Tous les champs sont obligatoires !");
        JLabel message2 = new JLabel("Employé non trouvé !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!rechercher_fld_sal.getText().trim().isEmpty()) {
            String searchQuery = "SELECT * FROM employé WHERE id_utilisateur IS NULL AND matricule = " + rechercher_fld_sal.getText();
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecQuery(searchQuery);

                if (c.rs.next()) {
                    nom_fld_sal.setText(c.rs.getString(2));
                    prénom_fld_sal.setText(c.rs.getString(3));
                    emploi_fld_sal.setText(c.rs.getString(4));
                    salaire_fld_sal.setText(c.rs.getString(10));
                    checkDateDernierPaiement();
                } else {
                    JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.ERROR_MESSAGE);
                    rechercher_fld_sal.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_rechercher_btn_salActionPerformed

    private void rechercher_btn_conActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercher_btn_conActionPerformed

        JLabel message = new JLabel("Tous les champs sont obligatoires !");
        JLabel message2 = new JLabel("Employé non trouvé !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!rechercher_fld_con.getText().trim().isEmpty()) {
            String searchQuery = "SELECT * FROM employé WHERE id_utilisateur IS NULL AND matricule = " + rechercher_fld_con.getText();
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecQuery(searchQuery);

                if (c.rs.next()) {
                    nom_fld_con.setText(c.rs.getString(2));
                    prénom_fld_con.setText(c.rs.getString(3));
                    emploi_fld_con.setText(c.rs.getString(4));
                } else {
                    JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.ERROR_MESSAGE);
                    rechercher_fld_con.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_rechercher_btn_conActionPerformed

    private void rechercher_btn_travActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercher_btn_travActionPerformed

        JLabel message = new JLabel("Tous les champs sont obligatoires !");
        JLabel message2 = new JLabel("Employé non trouvé !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!rechercher_fld_trav.getText().trim().isEmpty()) {
                                                                          //AND DATE SORTIE IS NULL
            String searchQuery = "SELECT * FROM employé WHERE id_utilisateur IS NULL AND matricule = " + rechercher_fld_trav.getText();
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecQuery(searchQuery);

                if (c.rs.next()) {
                    nom_fld_trav.setText(c.rs.getString(2));
                    prénom_fld_trav.setText(c.rs.getString(3));
                    emploi_fld_trav.setText(c.rs.getString(4));
                    date_embauche_trav.setDate(c.rs.getDate(8));
                } else {
                    JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.ERROR_MESSAGE);
                    rechercher_fld_trav.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_rechercher_btn_travActionPerformed

    private void rechercher_btn_histoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercher_btn_histoActionPerformed

        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);

        JLabel message = new JLabel("Tous les champs sont obligatoires !");
        JLabel message2 = new JLabel("Pas d'historique pour cet employé !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!rechercher_fld_histo.getText().trim().isEmpty()) {
            String searchQuery = "SELECT id_historique, u.identifiant, d.nom_document, e.nom, TO_CHAR(date_impression, 'dd/MM/yyyy HH:MI:SS')"
                    + " FROM utilisateur u, document d, employé e, historique h"
                    + " WHERE h.matricule = e.matricule"
                    + " AND h.id_utilisateur = u.id_utilisateur"
                    + " AND h.id_document = d.id_document"
                    + " AND h.matricule = " + rechercher_fld_histo.getText() + " ORDER BY id_historique";
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecQuery(searchQuery);
                //----------------------------------------//
//                String nom_document =  c.rs.getString(3);
//                String congéQuery = "SELECT type_congé FROM attcongé a, document d"
//                        + " WHERE a.id_document = d.id_document"
//                        + " AND d.nom_document = '"+nom_document+"'"
//                        + " AND matricule = "+rechercher_fld_histo.getText();
//                
//                MyConnection c2 = new MyConnection(false);
//                c2.MyExecQuery(congéQuery);

                //----------------------------------------//
                boolean data_not_found = true;
                
                while (c.rs.next()) 
                {
                        data_not_found = false;
                        dtm.addRow(new Object[]{
                            c.rs.getString(1),
                            c.rs.getString(2),
                            c.rs.getString(3),
                            c.rs.getString(4),
                            c.rs.getString(5)
                        });

//                    String nom_document = c.rs.getString(3);
//
//                    String congéQuery = "SELECT type_congé FROM attcongé a, document d"
//                            + " WHERE a.id_document = d.id_document"
//                            + " AND d.nom_document = '" + nom_document + "'"
//                            + " AND matricule = " + rechercher_fld_histo.getText();
//
//                    // hada howa lcode li khassek t3aweni fih 
//                    MyConnection c2 = new MyConnection(false);
//                    c2.MyExecQuery(congéQuery);
//                    while (c2.rs.next()) {
//                        for (int i = 0; i < dtm.getRowCount(); i++) {
//                            if (dtm.getValueAt(i, 2).equals("ATT_CONGÉ")) {
//                                dtm.setValueAt("ATT_" + c2.rs.getString(1), i, 2);
//                            }
//                        }
//                    }
                }

                int rc = dtm.getRowCount();
                nombre_lignes_label.setText(Integer.toString(rc));

                if (data_not_found == true) {
                    JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.ERROR_MESSAGE);
                    rechercher_fld_histo.setText("");
                    nombre_lignes_label.setText("0");
                    
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            fillHistoTable();
                int rc = dtm.getRowCount();
                nombre_lignes_label.setText(Integer.toString(rc));
        }
    }//GEN-LAST:event_rechercher_btn_histoActionPerformed

    private void rechercher_fld_travKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_travKeyTyped

        if (rechercher_fld_trav.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_rechercher_fld_travKeyTyped

    private void rechercher_fld_travKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_travKeyReleased

        if (!rechercher_fld_trav.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Matricule doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (rechercher_fld_trav.getText().trim().length() >= 5) {
                evt.consume();
            }

            char c = evt.getKeyChar();
            if (Character.isLetter(c) && !evt.isAltDown()) {
                evt.consume();
            }

            try {
                String matricule = rechercher_fld_trav.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(matricule);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                rechercher_fld_trav.setText("");
            }
        }
    }//GEN-LAST:event_rechercher_fld_travKeyReleased

    private void rechercher_fld_conKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_conKeyTyped

        if (rechercher_fld_con.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_rechercher_fld_conKeyTyped

    private void rechercher_fld_conKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_conKeyReleased

        if (!rechercher_fld_con.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Matricule doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (rechercher_fld_con.getText().trim().length() >= 5) {
                evt.consume();
            }

            char c = evt.getKeyChar();
            if (Character.isLetter(c) && !evt.isAltDown()) {
                evt.consume();
            }

            try {
                String matricule = rechercher_fld_con.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(matricule);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                rechercher_fld_con.setText("");
            }
        }
    }//GEN-LAST:event_rechercher_fld_conKeyReleased

    private void rechercher_fld_salKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_salKeyTyped

        if (rechercher_fld_sal.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_rechercher_fld_salKeyTyped

    private void rechercher_fld_salKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_salKeyReleased

        if (!rechercher_fld_sal.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Matricule doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (rechercher_fld_sal.getText().trim().length() >= 5) {
                evt.consume();
            }

            char c = evt.getKeyChar();
            if (Character.isLetter(c) && !evt.isAltDown()) {
                evt.consume();
            }

            try {
                String matricule = rechercher_fld_sal.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(matricule);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                rechercher_fld_sal.setText("");
            }
        }

    }//GEN-LAST:event_rechercher_fld_salKeyReleased

    private void prénom_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_prénom_fldKeyReleased

        if (!prénom_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Prénom doit être alphabétique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (!prénom_fld.getText().matches("^[a-zA-Z]+$")) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                prénom_fld.setText("");
            }
        }
    }//GEN-LAST:event_prénom_fldKeyReleased

    private void nom_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nom_fldKeyReleased

        if (!nom_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Nom doit être alphabétique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (!nom_fld.getText().matches("^[a-zA-Z]+$")) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                nom_fld.setText("");
            }
        }
    }//GEN-LAST:event_nom_fldKeyReleased

    private void rechercher_fld_histoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_histoKeyTyped

        if (rechercher_fld_histo.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_rechercher_fld_histoKeyTyped

    private void rechercher_fld_histoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_histoKeyReleased

        if (!rechercher_fld_histo.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Matricule doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (rechercher_fld_histo.getText().trim().length() >= 5) {
                evt.consume();
            }

            char c = evt.getKeyChar();
            if (Character.isLetter(c) && !evt.isAltDown()) {
                evt.consume();
            }

            try {
                String matricule = rechercher_fld_histo.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(matricule);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                rechercher_fld_histo.setText("");
            }
        }
    }//GEN-LAST:event_rechercher_fld_histoKeyReleased

    private void supprimer_histo_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimer_histo_btnActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment supprimer cette ligne ?");
        JLabel message2 = new JLabel("Historique supprimée avec succès !");
        JLabel message3 = new JLabel("Erreur lors de la suppression de cette ligne !");
        JLabel message4 = new JLabel("Sélectionner une ligne avant supprimer !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};
        
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        int row = jTable1.getSelectedRow();
        if(row >= 0)
        {
            String idHisto = dtm.getValueAt(row, 0).toString();
            String nomAtt = dtm.getValueAt(row, 2).toString();
            String deleteHisto = "DELETE FROM historique WHERE id_historique = "+idHisto;
       
            try
            {
                MyConnection c = new MyConnection(false);
                c.MyExecPrepStat(deleteHisto);
                int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    
                    try
                    {
                        MyConnection cc = new MyConnection(false);

                        if(nomAtt.equals("ATT_SALAIRE"))
                        {
                            cc.MyExecUpdate("DELETE FROM attestation_salaire WHERE id_historique = "+idHisto);
                            System.out.println("selected : "+nomAtt);
                        }
                            else if(nomAtt.equals("ATT_TRAVAIL"))
                            {
                                cc.MyExecUpdate("DELETE FROM attestation_travail WHERE id_historique = "+idHisto);
                                System.out.println("selected : "+nomAtt);
                            }
                                else if(nomAtt.startsWith("ATT_CONGÉ_"))
                                {
                                    cc.MyExecUpdate("DELETE FROM attestation_congé WHERE id_historique = "+idHisto);
                                    System.out.println("selected : "+nomAtt);
                                }
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    
                    int count = c.pst.executeUpdate();

                    if (count == 0) {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        dtm.removeRow(row);
                        dtm.setRowCount(0);
                        fillHistoTable();
                        int rc = dtm.getRowCount();
                        nombre_lignes_label.setText(Integer.toString(rc));
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_supprimer_histo_btnActionPerformed

    private void imprimer_btn_congéActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimer_btn_congéActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment imprimer cette attestation de congé ?");
        JLabel message2 = new JLabel("Impression en cours...");
        JLabel message3 = new JLabel("Erreur lors de l'impression de cette attestation !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");
        JLabel message5 = new JLabel("La date de fin doit être supérieure à la date de début !");
        
        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message5.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};
        
        String matricule = rechercher_fld_con.getText();
        String identifiant = menu_connected_user.getText();
        String nom_document = type_congé_combo.getSelectedItem().toString();
        int id_historique = 0;
        int id_document = 0;
        int id_utilisateur = 0;
        int id_att_con = 0;

        
        if(!nom_fld_con.getText().trim().isEmpty() && !prénom_fld_con.getText().trim().isEmpty() &&
           !emploi_fld_con.getText().trim().isEmpty() && type_congé_combo.getSelectedIndex() != 0 &&
            date_début_fld.getDate() != null && date_fin_fld.getDate() != null
          )
        {
            if(date_fin_fld.getDate().getTime() < date_début_fld.getDate().getTime())
            {
                JOptionPane.showMessageDialog(this, message5, "e-Document", JOptionPane.WARNING_MESSAGE);
            }
            else
            {
                //max id_historique
                try
                {
                    String maxIdHisto = "SELECT NVL(MAX(id_historique)+1,1) FROM historique";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(maxIdHisto);
                    if(c.rs.next())
                    {
                        id_historique = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                //max id_att_congé
                try
                {
                    String maxIdCongé = "SELECT NVL(MAX(id_att_con)+1,1) FROM attestation_congé";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(maxIdCongé);
                    if(c.rs.next())
                    {
                        id_att_con = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                // id_document
                try
                {
                    String idDoc = "SELECT id_document FROM document WHERE nom_document = CONCAT('ATT_', '"+nom_document+"')";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(idDoc);
                    if(c.rs.next())
                    {
                        id_document = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                // id_utilisateur
                try
                {
                    String idUti = "SELECT id_utilisateur FROM utilisateur WHERE identifiant = '"+identifiant+"' ";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(idUti);
                    if(c.rs.next())
                    {
                        id_utilisateur = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                //main query
                String mainQuery = "INSERT ALL INTO historique VALUES(?, ?, ?, ?, SYSDATE)"
                                           + " INTO attestation_congé VALUES(?, ?, ?, ?, ?, ?)"
                                           + " SELECT * FROM DUAL";
                try
                {
                    MyConnection c = new MyConnection(false);
                    c.MyExecPrepStat(mainQuery);
                    c.pst.setInt(1, id_historique);
                    c.pst.setInt(2, id_document);
                    c.pst.setInt(3, id_utilisateur);
                    c.pst.setInt(4, Integer.parseInt(matricule));
                    c.pst.setInt(5, id_att_con);
                    c.pst.setInt(6, id_historique);
                    c.pst.setDate(7, new java.sql.Date(date_début_fld.getDate().getTime()));
                    c.pst.setDate(8, new java.sql.Date(date_fin_fld.getDate().getTime()));
                    c.pst.setInt(9, id_document);
                    c.pst.setInt(10, Integer.parseInt(matricule));
                    
                    int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                    if (confirm == JOptionPane.YES_OPTION) 
                    {
                        int count = c.pst.executeUpdate();
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        
                        if(type_congé_combo.getSelectedIndex() == 1)
                        {
                            HashMap p = new HashMap();
                            p.put("matricule", Integer.parseInt(matricule));
                            p.put("date_début", new java.sql.Date(date_début_fld.getDate().getTime()));
                            p.put("date_fin", new java.sql.Date(date_fin_fld.getDate().getTime()));
                            JasperDesign jd = JRXmlLoader.load("ATT_CONGÉ_MALADIE.jrxml");
                            JasperReport jr = JasperCompileManager.compileReport(jd);
                            JasperPrint jp = JasperFillManager.fillReport(jr, p, c.con);
                            JasperViewer.viewReport(jp, false); 
                        }
                            else if(type_congé_combo.getSelectedIndex() == 2)
                            {
                                HashMap p = new HashMap();
                                p.put("matricule", Integer.parseInt(matricule));
                                p.put("date_début", new java.sql.Date(date_début_fld.getDate().getTime()));
                                p.put("date_fin", new java.sql.Date(date_fin_fld.getDate().getTime()));
                                JasperDesign jd = JRXmlLoader.load("ATT_CONGÉ_NAISSANCE.jrxml");
                                JasperReport jr = JasperCompileManager.compileReport(jd);
                                JasperPrint jp = JasperFillManager.fillReport(jr, p, c.con);
                                JasperViewer.viewReport(jp, false); 
                            }
                                else if(type_congé_combo.getSelectedIndex() == 3)
                                {
                                    HashMap p = new HashMap();
                                    p.put("matricule", Integer.parseInt(matricule));
                                    p.put("date_début", new java.sql.Date(date_début_fld.getDate().getTime()));
                                    p.put("date_fin", new java.sql.Date(date_fin_fld.getDate().getTime()));
                                    JasperDesign jd = JRXmlLoader.load("ATT_CONGÉ_MARIAGE.jrxml");
                                    JasperReport jr = JasperCompileManager.compileReport(jd);
                                    JasperPrint jp = JasperFillManager.fillReport(jr, p, c.con);
                                    JasperViewer.viewReport(jp, false); 
                                }

                        
                        if(count == 0)
                        {
                            JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_imprimer_btn_congéActionPerformed

    private void type_document_comboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_type_document_comboItemStateChanged

        nom_fld_con.setText("");
        nom_fld_sal.setText("");
        nom_fld_trav.setText("");
        
        prénom_fld_con.setText("");
        prénom_fld_trav.setText("");
        prénom_fld_sal.setText("");
        
        emploi_fld_con.setText("");
        emploi_fld_trav.setText("");
        emploi_fld_sal.setText("");
        
//        date_début_fld.setDate(null);
        date_fin_fld.setDate(null);
        
//        date_dpaiement_fld.setDate(null);
        date_paiement_fld.setDate(null);
        
       date_embauche_trav.setDate(null);
       date_sortie_trav.setDate(null);
       
       type_congé_combo.setSelectedIndex(0);
       salaire_fld_sal.setText("");
       
       rechercher_fld_con.setText("");
       rechercher_fld_trav.setText("");
       rechercher_fld_sal.setText("");
    }//GEN-LAST:event_type_document_comboItemStateChanged

    private void imprimer_btn_salActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimer_btn_salActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment imprimer cette attestation de salaire ?");
        JLabel message2 = new JLabel("Impression en cours...");
        JLabel message3 = new JLabel("Erreur lors de l'impression de cette attestation !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");
        JLabel message5 = new JLabel("La date de paiement doit être supérieure à la date dernier paiement !");
        
        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message5.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};
        
        String matricule = rechercher_fld_sal.getText();
        String identifiant = menu_connected_user.getText();
        String nom_document = type_document_combo.getSelectedItem().toString();
        int id_historique = 0;
        int id_document = 0;
        int id_utilisateur = 0;
        int id_att_sal = 0;

        
        if(!nom_fld_sal.getText().trim().isEmpty() && !prénom_fld_sal.getText().trim().isEmpty() &&
           !emploi_fld_sal.getText().trim().isEmpty() && !salaire_fld_sal.getText().trim().isEmpty() &&
            date_dpaiement_fld.getDate() != null && date_paiement_fld.getDate() != null
          )
        {
            if(date_paiement_fld.getDate().getTime() < date_dpaiement_fld.getDate().getTime())
            {
                JOptionPane.showMessageDialog(this, message5, "e-Document", JOptionPane.WARNING_MESSAGE);
            }
            else
            {
                //max id_historique
                try
                {
                    String maxIdHisto = "SELECT NVL(MAX(id_historique)+1,1) FROM historique";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(maxIdHisto);
                    if(c.rs.next())
                    {
                        id_historique = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                //max id_att_salaire
                try
                {
                    String maxIdSalaire = "SELECT NVL(MAX(id_att_sal)+1,1) FROM attestation_salaire";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(maxIdSalaire);
                    if(c.rs.next())
                    {
                        id_att_sal = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                // id_document
                try
                {
                    String idDoc = "SELECT id_document FROM document WHERE nom_document = '"+nom_document+"'";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(idDoc);
                    if(c.rs.next())
                    {
                        id_document = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                // id_utilisateur
                try
                {
                    String idUti = "SELECT id_utilisateur FROM utilisateur WHERE identifiant = '"+identifiant+"' ";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(idUti);
                    if(c.rs.next())
                    {
                        id_utilisateur = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                //main query
                String mainQuery = "INSERT ALL INTO historique VALUES(?, ?, ?, ?, SYSDATE)"
                                           + " INTO attestation_salaire VALUES(?, ?, ?, ?, ?, ?)"
                                           + " SELECT * FROM DUAL";
                try
                {
                    MyConnection c = new MyConnection(false);
                    c.MyExecPrepStat(mainQuery);
                    c.pst.setInt(1, id_historique);
                    c.pst.setInt(2, id_document);
                    c.pst.setInt(3, id_utilisateur);
                    c.pst.setInt(4, Integer.parseInt(matricule));
                    c.pst.setInt(5, id_att_sal);
                    c.pst.setInt(6, id_historique);
                    c.pst.setDate(7, new java.sql.Date(date_dpaiement_fld.getDate().getTime()));
                    c.pst.setDate(8, new java.sql.Date(date_paiement_fld.getDate().getTime()));
                    c.pst.setInt(9, id_document);
                    c.pst.setInt(10, Integer.parseInt(matricule));
                    
                    
                    int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                    if (confirm == JOptionPane.YES_OPTION) 
                    {
                        int count = c.pst.executeUpdate();
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        
                        HashMap p = new HashMap();
                        p.put("matricule", Integer.parseInt(matricule));
                        p.put("date_dernier_paiement", new java.sql.Date(date_dpaiement_fld.getDate().getTime()));
                        p.put("date_paiement", new java.sql.Date(date_paiement_fld.getDate().getTime()));
                        JasperDesign jd = JRXmlLoader.load("ATT_SALAIRE.jrxml");
                        JasperReport jr = JasperCompileManager.compileReport(jd);
                        JasperPrint jp = JasperFillManager.fillReport(jr, p, c.con);
                        JasperViewer.viewReport(jp, false);
                        
                        if(count == 0)
                        {
                            JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_imprimer_btn_salActionPerformed

    private void imprimer_btn_travActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimer_btn_travActionPerformed

      JLabel message = new JLabel("Voulez-vous vraiment imprimer cette attestation de travail ?");
        JLabel message2 = new JLabel("Impression en cours...");
        JLabel message3 = new JLabel("Erreur lors de l'impression de cette attestation !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");
        JLabel message5 = new JLabel("La date de sortie doit être supérieure à la date d'embauche !");
        
        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message5.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};
        
        String matricule = rechercher_fld_trav.getText();
        String identifiant = menu_connected_user.getText();
        String nom_document = type_document_combo.getSelectedItem().toString();
        int id_historique = 0;
        int id_document = 0;
        int id_utilisateur = 0;
        int id_att_trav = 0;

        
        if(!nom_fld_trav.getText().trim().isEmpty() && !prénom_fld_trav.getText().trim().isEmpty() &&
           !emploi_fld_trav.getText().trim().isEmpty() && date_embauche_trav.getDate() != null && 
            date_sortie_trav.getDate() != null
          )
        {
            if(date_sortie_trav.getDate().getTime() < date_embauche_trav.getDate().getTime())
            {
                JOptionPane.showMessageDialog(this, message5, "e-Document", JOptionPane.WARNING_MESSAGE);
            }
            else
            {
                //max id_historique
                try
                {
                    String maxIdHisto = "SELECT NVL(MAX(id_historique)+1,1) FROM historique";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(maxIdHisto);
                    if(c.rs.next())
                    {
                        id_historique = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                //max id_att_travail
                try
                {
                    String maxIdTravail = "SELECT NVL(MAX(id_att_tra)+1,1) FROM attestation_travail";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(maxIdTravail);
                    if(c.rs.next())
                    {
                        id_att_trav = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                // id_document
                try
                {
                    String idDoc = "SELECT id_document FROM document WHERE nom_document = '"+nom_document+"'";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(idDoc);
                    if(c.rs.next())
                    {
                        id_document = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                // id_utilisateur
                try
                {
                    String idUti = "SELECT id_utilisateur FROM utilisateur WHERE identifiant = '"+identifiant+"' ";
                    MyConnection c = new MyConnection(false);
                    c.MyExecQuery(idUti);
                    if(c.rs.next())
                    {
                        id_utilisateur = c.rs.getInt(1);
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                //-----------------------------------------------//
                
                //main query
                String mainQuery = "INSERT ALL INTO historique VALUES(?, ?, ?, ?, SYSDATE)"
                                           + " INTO attestation_travail VALUES(?, ?, ?, ?, ?, ?)"
                                           + " SELECT * FROM DUAL";
                try
                {
                    MyConnection c = new MyConnection(false);
                    c.MyExecPrepStat(mainQuery);
                    c.pst.setInt(1, id_historique);
                    c.pst.setInt(2, id_document);
                    c.pst.setInt(3, id_utilisateur);
                    c.pst.setInt(4, Integer.parseInt(matricule));
                    c.pst.setInt(5, id_att_trav);
                    c.pst.setInt(6, id_historique);
                    c.pst.setDate(7, new java.sql.Date(date_embauche_trav.getDate().getTime()));
                    c.pst.setDate(8, new java.sql.Date(date_sortie_trav.getDate().getTime()));
                    c.pst.setInt(9, id_document);
                    c.pst.setInt(10, Integer.parseInt(matricule));
                    
                    int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                    if (confirm == JOptionPane.YES_OPTION) 
                    {
                        int count = c.pst.executeUpdate();
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        
                        HashMap p = new HashMap();
                        p.put("matricule", Integer.parseInt(matricule));
                        p.put("date_embauche", new java.sql.Date(date_embauche_trav.getDate().getTime()));
                        p.put("date_sortie", new java.sql.Date(date_sortie_trav.getDate().getTime()));
                        JasperDesign jd = JRXmlLoader.load("ATT_TRAVAIL.jrxml");
                        JasperReport jr = JasperCompileManager.compileReport(jd);
                        JasperPrint jp = JasperFillManager.fillReport(jr, p, c.con);
                        JasperViewer.viewReport(jp, false);
                        
                        //update date_sortie table employé
                            try
                            {
                                String updateEmployé = "UPDATE employé SET date_sortie = ? WHERE matricule = ?";
                                MyConnection cc = new MyConnection(false);
                                cc.MyExecPrepStat(updateEmployé);
                                cc.pst.setDate(1, new java.sql.Date(date_sortie_trav.getDate().getTime()));
                                cc.pst.setInt(2, Integer.parseInt(matricule));
                                cc.pst.executeUpdate();
                            }
                            catch(Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        
                        if(count == 0)
                        {
                            JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_imprimer_btn_travActionPerformed

    private void type_congé_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_congé_comboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_type_congé_comboActionPerformed

    private void adresse_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adresse_fldKeyReleased

        if (!adresse_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Adresse doit être alphanumérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (!adresse_fld.getText().matches("^[a-zA-Z0-9]+$")) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                adresse_fld.setText("");
            }
        }
    }//GEN-LAST:event_adresse_fldKeyReleased

    private void supprimer_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimer_btn_utiActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment supprimer cet utilisateur ?");
        JLabel message2 = new JLabel("Utilisateur supprimé avec succès !");
        JLabel message3 = new JLabel("Erreur lors de la suppression de cet utilisateur !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};

        if (!id_utilisateur_fld.getText().trim().isEmpty() && !identifiant_fld_uti.getText().trim().isEmpty()
            && !motdepasse_fld_uti.getText().trim().isEmpty() && !type_utilisateur_fld.getText().trim().isEmpty()) {

            String deleteQuery = "DELETE FROM utilisateur WHERE id_utilisateur = " + Integer.parseInt(id_utilisateur_fld.getText());
            try {
                MyConnection c = new MyConnection(false);
                int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    int count = c.MyExecUpdate(deleteQuery);

                    if (count == 0) {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        RefreshUsersList();
                        firstUser();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_supprimer_btn_utiActionPerformed

    private void nouveau_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nouveau_btn_utiActionPerformed

        try {
            MyConnection c = new MyConnection(false);
            c.MyExecQuery("SELECT NVL(MAX(id_utilisateur)+1,1) FROM utilisateur");
            if (c.rs.next()) {
                id_utilisateur_fld.setText(c.rs.getString(1));
            }
            rechercher_fld_uti.setText("");
            type_utilisateur_fld.setText("");
            identifiant_fld_uti.setText("");
            motdepasse_fld_uti.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_nouveau_btn_utiActionPerformed

    private void enregistrer_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enregistrer_btn_utiActionPerformed

        JLabel message = new JLabel("Un utilisateur existe déjà avec cet ID !");
        JLabel message2 = new JLabel("Utilisateur ajouté avec succès !");
        JLabel message3 = new JLabel("Erreur lors de l'ajout de cet utilisateur !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!id_utilisateur_fld.getText().trim().isEmpty() && !identifiant_fld_uti.getText().trim().isEmpty()
            && !motdepasse_fld_uti.getText().trim().isEmpty() && !type_utilisateur_fld.getText().trim().isEmpty()) {
            try {
                int id_utilisateur = Integer.parseInt(id_utilisateur_fld.getText());
                String identifiant = identifiant_fld_uti.getText();
                String motdepasse = motdepasse_fld_uti.getText();
                String type = type_utilisateur_fld.getText();

                String insertQuery = "INSERT INTO utilisateur VALUES(?, ?, ?, ?)";

                MyConnection c = new MyConnection(false);
                c.MyExecPrepStat(insertQuery);
                c.pst.setInt(1, id_utilisateur);
                c.pst.setString(2, identifiant);
                c.pst.setString(3, motdepasse);
                c.pst.setString(4, type);

                if (checkDuplicateUser(id_utilisateur_fld.getText())) {
                    JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (c.pst.executeUpdate() == 1) {
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        RefreshUsersList();
                        firstUser();
                    } else {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_enregistrer_btn_utiActionPerformed

    private void modifier_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifier_btn_utiActionPerformed

        JLabel message = new JLabel("Voulez-vous vraiment modifier cet utilisateur ?");
        JLabel message2 = new JLabel("Utilisateur modifié avec succès !");
        JLabel message3 = new JLabel("Erreur lors de la modification de cet utilisateur !");
        JLabel message4 = new JLabel("Tous les champs sont obligatoires !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message3.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message4.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        String options[] = new String[]{"Oui", "Non"};

        if (!id_utilisateur_fld.getText().trim().isEmpty() && !identifiant_fld_uti.getText().trim().isEmpty()
            && !motdepasse_fld_uti.getText().trim().isEmpty() && !type_utilisateur_fld.getText().trim().isEmpty()) {

            String updateQuery = "UPDATE utilisateur SET type = ?, identifiant = ?, mot_de_passe = ?"
            + " WHERE id_utilisateur = ?";
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecPrepStat(updateQuery);
                c.pst.setString(1, type_utilisateur_fld.getText());
                c.pst.setString(2, identifiant_fld_uti.getText());
                c.pst.setString(3, motdepasse_fld_uti.getText());
                c.pst.setInt(4, Integer.parseInt(id_utilisateur_fld.getText()));

                int confirm = JOptionPane.showOptionDialog(this, message, "e-Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    int count = c.pst.executeUpdate();

                    if (count == 0) {
                        JOptionPane.showMessageDialog(this, message3, "e-Document", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.INFORMATION_MESSAGE);
                        RefreshUsersList();
                        firstUser();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message4, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_modifier_btn_utiActionPerformed

    private void rechercher_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercher_btn_utiActionPerformed

        JLabel message = new JLabel("Tous les champs sont obligatoires !");
        JLabel message2 = new JLabel("Utilisateur non trouvé !");

        message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
        message2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

        if (!rechercher_fld_uti.getText().trim().isEmpty()) {
            String searchQuery = "SELECT * FROM utilisateur WHERE type <> 'Administrateur' AND id_utilisateur = " + rechercher_fld_uti.getText();
            try {
                MyConnection c = new MyConnection(false);
                c.MyExecQuery(searchQuery);

                if (c.rs.next()) {
                    id_utilisateur_fld.setText(c.rs.getString(1));
                    identifiant_fld_uti.setText(c.rs.getString(2));
                    motdepasse_fld_uti.setText(c.rs.getString(3));
                    type_utilisateur_fld.setText(c.rs.getString(4));

                } else {
                    JOptionPane.showMessageDialog(this, message2, "e-Document", JOptionPane.ERROR_MESSAGE);
                    rechercher_fld_uti.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_rechercher_btn_utiActionPerformed

    private void rechercher_fld_utiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_utiKeyTyped

        if (rechercher_fld_uti.getText().trim().length() >= 5) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isLetter(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_rechercher_fld_utiKeyTyped

    private void rechercher_fld_utiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercher_fld_utiKeyReleased

        if (!rechercher_fld_uti.getText().trim().isEmpty()) {
            JLabel message = new JLabel("ID utilistateur doit être numérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (rechercher_fld_uti.getText().trim().length() >= 5) {
                evt.consume();
            }

            char c = evt.getKeyChar();
            if (Character.isLetter(c) && !evt.isAltDown()) {
                evt.consume();
            }

            try {
                String id_utilisateur = rechercher_fld_uti.getText();
                boolean incorrect = true;

                while (incorrect) {
                    Integer.parseInt(id_utilisateur);
                    incorrect = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                rechercher_fld_uti.setText("");
            }
        }
    }//GEN-LAST:event_rechercher_fld_utiKeyReleased

    private void next_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next_btn_utiActionPerformed

        try {
            if (cnav2.rs.next()) {
                id_utilisateur_fld.setText(cnav2.rs.getString(1));
                identifiant_fld_uti.setText(cnav2.rs.getString(2));
                motdepasse_fld_uti.setText(cnav2.rs.getString(3));
                type_utilisateur_fld.setText(cnav2.rs.getString(4));

                first_btn_uti.setEnabled(true);
                previous_btn_uti.setEnabled(true);
            }

            if (cnav2.rs.isLast()) {
                next_btn_uti.setEnabled(false);
                last_btn_uti.setEnabled(false);
                first_btn_uti.setEnabled(true);
                previous_btn_uti.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_next_btn_utiActionPerformed

    private void last_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_last_btn_utiActionPerformed

        try {
            if (cnav2.rs.last()) {
                id_utilisateur_fld.setText(cnav2.rs.getString(1));
                identifiant_fld_uti.setText(cnav2.rs.getString(2));
                motdepasse_fld_uti.setText(cnav2.rs.getString(3));
                type_utilisateur_fld.setText(cnav2.rs.getString(4));
            }

            if (cnav2.rs.isLast()) {
                last_btn_uti.setEnabled(false);
                next_btn_uti.setEnabled(false);
                previous_btn_uti.setEnabled(true);
                first_btn_uti.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_last_btn_utiActionPerformed

    private void previous_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previous_btn_utiActionPerformed

        try {
            if (cnav2.rs.previous()) {
                id_utilisateur_fld.setText(cnav2.rs.getString(1));
                identifiant_fld_uti.setText(cnav2.rs.getString(2));
                motdepasse_fld_uti.setText(cnav2.rs.getString(3));
                type_utilisateur_fld.setText(cnav2.rs.getString(4));

                next_btn_uti.setEnabled(true);
                last_btn_uti.setEnabled(true);
            }

            if (cnav2.rs.isFirst()) {
                first_btn_uti.setEnabled(false);
                previous_btn_uti.setEnabled(false);
                next_btn_uti.setEnabled(true);
                last_btn_uti.setEnabled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_previous_btn_utiActionPerformed

    private void first_btn_utiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_first_btn_utiActionPerformed

        firstUser();
    }//GEN-LAST:event_first_btn_utiActionPerformed

    private void id_utilisateur_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_id_utilisateur_fldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_id_utilisateur_fldKeyTyped

    private void motdepasse_fld_utiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_motdepasse_fld_utiKeyTyped

        if (motdepasse_fld_uti.getText().trim().length() >= 10) {
            evt.consume();
        }
    }//GEN-LAST:event_motdepasse_fld_utiKeyTyped

    private void type_utilisateur_fldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_type_utilisateur_fldKeyTyped

        if (type_utilisateur_fld.getText().trim().length() >= 15) {
            evt.consume();
        }

        char c = evt.getKeyChar();
        if (Character.isDigit(c) && !evt.isAltDown()) {
            evt.consume();
        }
    }//GEN-LAST:event_type_utilisateur_fldKeyTyped

    private void type_utilisateur_fldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_type_utilisateur_fldKeyReleased

        if (!type_utilisateur_fld.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Type d'utilisateur doit être alphabétique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (!type_utilisateur_fld.getText().matches("^[a-zA-Z]+$")) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                type_utilisateur_fld.setText("");
            }
        }
    }//GEN-LAST:event_type_utilisateur_fldKeyReleased

    private void identifiant_fld_utiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_identifiant_fld_utiKeyTyped

        if (identifiant_fld_uti.getText().trim().length() >= 10) {
            evt.consume();
        }

        char keyChar = evt.getKeyChar();
        if (Character.isLowerCase(keyChar)) {
            evt.setKeyChar(Character.toUpperCase(keyChar));
        }
    }//GEN-LAST:event_identifiant_fld_utiKeyTyped

    private void identifiant_fld_utiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_identifiant_fld_utiKeyReleased

        if (!identifiant_fld_uti.getText().trim().isEmpty()) {
            JLabel message = new JLabel("Identifiant doit être alphanumérique !");
            message.setFont(new Font("Lucida Sans", Font.PLAIN, 14));

            if (!identifiant_fld_uti.getText().matches("^[a-zA-Z0-9]+$")) {
                JOptionPane.showMessageDialog(this, message, "e-Document", JOptionPane.WARNING_MESSAGE);
                identifiant_fld_uti.setText("");
            }
        }
    }//GEN-LAST:event_identifiant_fld_utiKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea adresse_fld;
    private javax.swing.JPanel congé;
    private com.toedter.calendar.JDateChooser date_dpaiement_fld;
    private com.toedter.calendar.JDateChooser date_début_fld;
    private com.toedter.calendar.JDateChooser date_embauche_fld;
    private com.toedter.calendar.JDateChooser date_embauche_trav;
    private com.toedter.calendar.JDateChooser date_fin_fld;
    private com.toedter.calendar.JDateChooser date_paiement_fld;
    private com.toedter.calendar.JDateChooser date_sortie_fld;
    private com.toedter.calendar.JDateChooser date_sortie_trav;
    private javax.swing.JPanel divers;
    private javax.swing.JPanel documents;
    private javax.swing.JButton déconnecter_btn;
    private javax.swing.JTextField email_fld;
    private javax.swing.JComboBox emploi_combo;
    private javax.swing.JTextField emploi_fld_con;
    private javax.swing.JTextField emploi_fld_sal;
    private javax.swing.JTextField emploi_fld_trav;
    private javax.swing.JButton enregistrer_btn;
    private javax.swing.JButton enregistrer_btn_uti;
    private javax.swing.JButton first_btn;
    private javax.swing.JButton first_btn_uti;
    private javax.swing.JPanel historique;
    private javax.swing.JTextField id_utilisateur_fld;
    private javax.swing.JTextField identifiant_fld_uti;
    private javax.swing.JButton imprimer_btn_congé;
    private javax.swing.JButton imprimer_btn_sal;
    private javax.swing.JButton imprimer_btn_trav;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JButton last_btn;
    private javax.swing.JButton last_btn_uti;
    private javax.swing.JPanel manuel;
    private javax.swing.JTextField matricule_fld;
    public static javax.swing.JLabel menu_connected_user;
    private javax.swing.JButton modifier_btn;
    private javax.swing.JButton modifier_btn_uti;
    private javax.swing.JButton mon_compte_btn;
    private javax.swing.JTextField motdepasse_fld_uti;
    private javax.swing.JButton next_btn;
    private javax.swing.JButton next_btn_uti;
    private javax.swing.JTextField nom_fld;
    private javax.swing.JTextField nom_fld_con;
    private javax.swing.JTextField nom_fld_sal;
    private javax.swing.JTextField nom_fld_trav;
    private javax.swing.JLabel nombre_lignes_label;
    private javax.swing.JButton nouveau_btn;
    private javax.swing.JButton nouveau_btn_uti;
    private javax.swing.JPanel parentPanel;
    private javax.swing.JButton previous_btn;
    private javax.swing.JButton previous_btn_uti;
    private javax.swing.JTextField prénom_fld;
    private javax.swing.JTextField prénom_fld_con;
    private javax.swing.JTextField prénom_fld_sal;
    private javax.swing.JTextField prénom_fld_trav;
    private javax.swing.JButton rechercher_btn;
    private javax.swing.JButton rechercher_btn_con;
    private javax.swing.JButton rechercher_btn_histo;
    private javax.swing.JButton rechercher_btn_sal;
    private javax.swing.JButton rechercher_btn_trav;
    private javax.swing.JButton rechercher_btn_uti;
    private javax.swing.JTextField rechercher_fld;
    private javax.swing.JTextField rechercher_fld_con;
    private javax.swing.JTextField rechercher_fld_histo;
    private javax.swing.JTextField rechercher_fld_sal;
    private javax.swing.JTextField rechercher_fld_trav;
    private javax.swing.JTextField rechercher_fld_uti;
    private javax.swing.JPanel salaire;
    private javax.swing.JTextField salaire_fld;
    private javax.swing.JTextField salaire_fld_sal;
    private javax.swing.JPanel salariés;
    private javax.swing.JButton supprimer_btn;
    private javax.swing.JButton supprimer_btn_uti;
    public static javax.swing.JButton supprimer_histo_btn;
    private javax.swing.JPanel travail;
    private javax.swing.JComboBox type_congé_combo;
    private javax.swing.JComboBox type_document_combo;
    private javax.swing.JTextField type_utilisateur_fld;
    private javax.swing.JTextField tél_fld;
    public static javax.swing.JPanel utilisateurs;
    private javax.swing.JPanel à_propos;
    // End of variables declaration//GEN-END:variables
}
