package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class Okno extends JFrame {
    // dane do nawiązania komunikacji z bazą danych
    private String jdbcUrl = "jdbc:mysql://localhost:3306/ksiegarnia?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", jdbcUser = "root", jdbcPass = "1234";

    // pole na komunikaty od aplikacji
    private JTextField komunikat = new JTextField();

    // panel z zakładkami
    private JTabbedPane tp = new JTabbedPane();
    private JPanel p_kli = new JPanel(); // klienci
    private JPanel p_ksi = new JPanel(); // ksiązki
    private JPanel p_zam = new JPanel(); // zamówiemia


    // panel dla zarządzania klientami
    private JTextField pole_pesel = new JTextField();
    private JTextField pole_im = new JTextField();
    private JTextField pole_naz = new JTextField();
    private JTextField pole_ur = new JTextField();
    private JTextField pole_mail = new JTextField();
    private JTextField pole_adr = new JTextField();
    private JTextField pole_tel = new JTextField();
    private JButton przyc_zapisz_kli = new JButton("zapisz");
    private JButton przyc_usun_kli = new JButton("usuń");
    private DefaultListModel<String> lmodel_kli = new DefaultListModel<>();
    private JList<String> l_kli = new JList<>(lmodel_kli);
    private JScrollPane sp_kli = new JScrollPane(l_kli);

    // panel dla zarządzania ksiazkami
    private JTextField pole_isbn = new JTextField();
    private JTextField pole_autor = new JTextField();
    private JTextField pole_tytul = new JTextField();
    private JComboBox lista_typ = new JComboBox();
    private JTextField pole_wydawnictwo = new JTextField();
    private JTextField pole_rok = new JTextField();
    private JTextField pole_cena = new JTextField();
    private JTextField pole_nowa_cena = new JTextField();
    private JButton przyc_nowa_cena = new JButton("zmień cenę");


    private JButton przyc_zapisz_ksi = new JButton("zapisz");
    private JButton przyc_usun_ksi = new JButton("usuń");
    private DefaultListModel<String> lmodel_ksi = new DefaultListModel<>();
    private JList<String> l_ksi = new JList<>(lmodel_ksi);
    private JScrollPane sp_ksi = new JScrollPane(l_ksi);


    // panel dla zarządzania zamoweniami
    private DefaultListModel<String> lmodel_ksi_zam = new DefaultListModel<>();
    private JList<String> lista_ksi_zam = new JList<>(lmodel_ksi_zam);
    private JScrollPane sp_ksi_zam = new JScrollPane(lista_ksi_zam);

    private DefaultListModel<String> lmodel_kli_zam = new DefaultListModel<>();
    private JList<String> lista_kli_zam = new JList<>(lmodel_kli_zam);
    private JScrollPane sp_kli_zam = new JScrollPane(lista_kli_zam);

    private DefaultListModel<String> lmodel_zam = new DefaultListModel<>();
    private JList<String> lista_zam = new JList<>(lmodel_zam);
    private JScrollPane sp_zam = new JScrollPane(lista_zam);


    private JTextField pole_kiedy = new JTextField("Rok-miesiąc-dzień");
    private JComboBox lista_status = new JComboBox();

    private JButton przyc_zrob_zamowienie = new JButton("zrób zamówienie");
    private JButton przyc_usun_zamowienia = new JButton("usuń zamówienie");
    private JButton przyc_zmien_status = new JButton("zmień status zamówienia");



    // funkcja aktualizująca listę klientów
    private void AktualnaListaKlientów(JList<String> lis) {
        try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT klienci.pesel, nazwisko, imie, adres FROM klienci, kontakty WHERE klienci.pesel = kontakty.pesel ORDER BY nazwisko, imie";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_kli.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + ", " + res.getString(4);
                lmodel_kli.addElement(s);
            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów "+ex);
            System.out.println(ex);
        }
    }
    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz klienta'
    private ActionListener akc_zap_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String pesel = pole_pesel.getText();
            if (! pesel.matches("[0-9]{3,11}")) {
                JOptionPane.showMessageDialog(Okno.this, "błąd w polu z peselm");
                pole_pesel.setText("");
                pole_pesel.requestFocus();
                return;
            }
            String imie = pole_im.getText();
            String nazwisko = pole_naz.getText();
            String ur = pole_ur.getText();
            if (imie.equals("") || nazwisko.equals("") || ur.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z imieniem lub nazwiskiem lub datą urodzenia");
                return;
            }
            String mail = pole_mail.getText();
            String adr = pole_adr.getText();
            String tel = pole_tel.getText();
            if (mail.equals("") || adr.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z emailem lub adresem");
                return;
            }
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql1 = "INSERT INTO klienci (pesel, imie, nazwisko, ur) VALUES('" + pole_pesel.getText() + "', '" + pole_im.getText() + "', '" + pole_naz.getText() + "', '" + pole_ur.getText() + "')";
                int res = stmt.executeUpdate(sql1);
                if (res == 1) {
                    komunikat.setText("OK - klient dodany do bazy");
                    String sql2 = "INSERT INTO kontakty (pesel, mail, adres, tel) VALUES('" + pole_pesel.getText() + "', '" + pole_mail.getText() + "', '" + pole_adr.getText() + "', '" + pole_tel.getText() + "')";
                    stmt.executeUpdate(sql2);
                    AktualnaListaKlientów(l_kli);
                }
            }
            catch(SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano klienta "+ex);
                System.out.println(ex);
            }
        }
    };
    // delegat obsługujący zdarzenie akcji od przycisku 'usuń klienta'
    private ActionListener akc_usun_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (l_kli.getSelectedIndices().length == 0) return;
            String p = l_kli.getModel().getElementAt(l_kli.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT COUNT(*) FROM zamowienia WHERE pesel = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0) {
                    String sql1 = "DELETE FROM klienci WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    String sql2 = "DELETE FROM kontakty WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql2);
                    komunikat.setText("OK - klient usunięty bazy");
                    AktualnaListaKlientów(l_kli);
                }
                else komunikat.setText("nie usunięto klienta, ponieważ składał już zamówienia");
            }
            catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie ununięto klienta "+ex);
                System.out.println(ex);
            }
        }
    };

    ////////////////////KSIAZKI FUNKCJI///////////////////////////////////////////////
    // funkcja aktualizująca listę ksiazek
    private void AktualnaListaKsiazek(JList<String> lis) {
        try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT ksiazki.isbn, autor, tytul, cena FROM ksiazki WHERE ksiazki.isbn = ksiazki.isbn ORDER BY autor, tytul";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_ksi.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + ", " + res.getString(3) + ", " + res.getString(4) + ", ";
                lmodel_ksi.addElement(s);
            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy książek "+ex);
            System.out.println(ex);
        }
    }
    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz księzkę'
    private ActionListener akc_zap_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String isbn = pole_isbn.getText();
            if (! isbn.matches("[0-9]{3,11}")) {
                JOptionPane.showMessageDialog(Okno.this, "błąd w polu z isbn");
                pole_isbn.setText("");
                pole_isbn.requestFocus();
                return;
            }
            String autor = pole_autor.getText();
            String tytul = pole_tytul.getText();
            typksiazki typksi = (typksiazki)lista_typ.getItemAt
                    (lista_typ.getSelectedIndex());
            String typ = typksi.name();
            if (autor.equals("") || tytul.equals("") || typ.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z autorem lub tytułem lub typem");
                return;
            }
            String wydawnictwo = pole_wydawnictwo.getText();
            String rok = pole_rok.getText();
            String cena = pole_cena.getText();
            if ( wydawnictwo.equals("") || rok.equals("") || cena.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z wydawnictwem lub rokiem lub ceną");
                return;
            }
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sqlksi = "INSERT INTO ksiazki (isbn, autor, tytul, typ, wydawnictwo, rok, cena) VALUES('" + pole_isbn.getText() + "', '" + pole_autor.getText() + "', '" + pole_tytul.getText() + "', '" + typ + "', '" + pole_wydawnictwo.getText() + "', '" + pole_rok.getText() + "', '" + pole_cena.getText() + "')";
                int res = stmt.executeUpdate(sqlksi);
                if (res == 1) {
                    komunikat.setText("OK - książka dodana do bazy");
                    AktualnaListaKsiazek(l_ksi);
                }
            }
            catch(SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano książkę "+ex);
                System.out.println(ex);
            }
        }
    };
    // delegat obsługujący zdarzenie akcji od przycisku 'usuń książkę'
    private ActionListener akc_usun_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (l_ksi.getSelectedIndices().length == 0) return;
            String p = l_ksi.getModel().getElementAt(l_ksi.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                ///!!!
                String sql = "SELECT COUNT(*) FROM zestawienia WHERE isbn = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0) {
                    String sql1 = "DELETE FROM ksiazki WHERE isbn = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    komunikat.setText("OK - książka usunięta z bazy");
                    AktualnaListaKsiazek(l_ksi);
                }
                else komunikat.setText("nie usunięto książkę, bo jest w zestawieniu");
            }
            catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie ununięto książki "+ex);
                System.out.println(ex);
            }
        }
    };

    // delegat obsługujący zdarzenie akcji od przycisku 'zmień cenę'
    private ActionListener akc_nowa_cena = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String nowaCena = pole_nowa_cena.getText();
            if (l_ksi.getSelectedIndices().length == 0 || nowaCena.equals("") ) return;
            String p = l_ksi.getModel().getElementAt(l_ksi.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();

                String sql = "UPDATE ksiazki SET cena = '" + nowaCena + "' WHERE isbn = '" + p + "'";
                int res = stmt.executeUpdate(sql);
                if (res == 1) {
                    komunikat.setText("OK - cena została zmieniona w bazie danych");
                    AktualnaListaKsiazek(l_ksi);
                }
                else komunikat.setText("nie zmieniono cene, bo jest w zestawieniu");
            }
            catch (SQLException ex) {
                komunikat.setText("błąd SQL - cena nie zoatała zapisana "+ex);
                System.out.println(ex);
            }
        }
    };

    /////////////FUNKCJI ZAMOWIENIA////////////////
    private void AktualnaListaKlientówZamow() {
        try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT klienci.pesel, nazwisko, imie FROM klienci ORDER BY nazwisko, imie";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_kli_zam.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3);
                lmodel_kli_zam.addElement(s);
            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy zamówień "+ex);
            System.out.println(ex);
        }
    }
    private void AktualnaListaKsiazekZamow() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT isbn, autor, tytul, cena FROM ksiazki ORDER BY tytul";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_ksi_zam.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3);
                lmodel_ksi_zam.addElement(s);
            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy zamówień");
        }
    }

    private void AktualnaListaZamow() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            // TO:DO add counter for order books
            String sql = "SELECT `id`, `pesel`, `kiedy`, `status` FROM `zamowienia` ORDER BY `kiedy`";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_zam.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4);
                lmodel_zam.addElement(s);
            }
        }
        catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy zamówień");
        }
    }

    private ActionListener akc_zrob_zam = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String dataZam = pole_kiedy.getText();
            if (!dataZam.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")){
                JOptionPane.showMessageDialog(Okno.this, "Data [____-__-__] ");
                pole_kiedy.setText("");
                pole_kiedy.requestFocus();
                return;
            }

            String ZamStatus = "zapłacone";

            if (lista_ksi_zam.getSelectedIndices().length == 0 || lista_kli_zam.getSelectedIndices().length == 0)
                return;

            String p = lista_kli_zam.getModel().getElementAt(lista_kli_zam.getSelectionModel().getMinSelectionIndex());

            String ZamPKKliPesel = p.substring(0, p.indexOf(':'));
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();

                System.out.println( "INSERT INTO `zamowienia`(`pesel`, `kiedy`, `status`) VALUES ('" + ZamPKKliPesel + "', '" + dataZam + "', '" + ZamStatus + "')");
                String SqlZamInsert = "INSERT INTO `zamowienia`(`pesel`, `kiedy`, `status`) VALUES ('" + ZamPKKliPesel + "', '" + dataZam + "', '" + ZamStatus + "')";
                int resInsertOrder = stmt.executeUpdate(SqlZamInsert);

                System.out.println("SELECT `id` FROM `zamowienia` WHERE `pesel`= " + ZamPKKliPesel + " AND `kiedy` = '" + dataZam + "' AND `status` = '" + ZamStatus + "'");
                String SqlZamSelectPK = "SELECT `id` FROM `zamowienia` WHERE `pesel`= " + ZamPKKliPesel + " AND `kiedy` = '" + dataZam + "' AND `status` = '" + ZamStatus + "'";
                ResultSet resSelectZamPK = stmt.executeQuery(SqlZamSelectPK);
                resSelectZamPK.next();
                int ZamPK = resSelectZamPK.getInt(1);

                for (int i = 0; i < lista_ksi_zam.getSelectedIndices().length; i++) {
                    String ZamKsi = lista_ksi_zam.getModel().getElementAt(i);

                    String ZamPKKsiISBN = ZamKsi.substring(0, ZamKsi.indexOf(':'));
                    System.out.println("SELECT `cena` FROM `ksiazki` WHERE `isbn`=" + ZamPKKsiISBN);
                    String ZamKsiSQLSelect = "SELECT `cena` FROM `ksiazki` WHERE `isbn`=" + ZamPKKsiISBN;
                    ResultSet resSelectZamKsiCena = stmt.executeQuery(ZamKsiSQLSelect);
                    resSelectZamKsiCena.next();
                    double ZamKsiCena = resSelectZamKsiCena.getDouble(1);

                    System.out.println("INSERT INTO `zestawienia`(`id`, `isbn`, `cena`) VALUES (" + ZamPK + ", " + ZamPKKsiISBN + " ," + ZamKsiCena + ")");
                    String SqlInsertZamKsi = "INSERT INTO `zestawienia`(`id`, `isbn`, `cena`) VALUES (" + ZamPK + ", " + ZamPKKsiISBN + " ," + ZamKsiCena + ")";
                    int resInsertZamKsi = stmt.executeUpdate(SqlInsertZamKsi);
                }

                if (resInsertOrder == 1) {
                    komunikat.setText("OK - listę zuaktylizowano");
                    AktualnaListaZamow();
                } else {
                    komunikat.setText("nie zmieniono listę");
                }
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie ununięto listy zamówień");
            }
        }
    };

    private ActionListener akc_zmien_status = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            statusKsiazki statusksi;
            statusksi = (statusKsiazki)lista_status .getItemAt(lista_status.getSelectedIndex());
            String ZamStatus = statusksi.name();


            komunikat.setText(lista_zam.getModel().getElementAt(lista_zam.getSelectionModel().getMinSelectionIndex()));
            if (lista_zam.getSelectedIndices().length == 0)
                return;
            String SqlSelectZam = lista_zam.getModel().getElementAt(lista_zam.getSelectionModel().getMinSelectionIndex());
            String ZamPK = SqlSelectZam.substring(0, SqlSelectZam.indexOf(':'));

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String SqlUpdateZam = "UPDATE `zamowienia` SET `status`='" + ZamStatus + "' WHERE `id`='" + ZamPK + "'";
                int res = stmt.executeUpdate(SqlUpdateZam);
                if (res == 1) {
                    komunikat.setText("OK - status zamówienia zmieniony");
                    AktualnaListaZamow();
                } else {
                    komunikat.setText("nie zmieniony status zamówienia");
                }
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zmieniono status zamówienia");
            }
        }
    };






    public Okno() throws SQLException {
        super("Księgarnia wysyłkowa");
        setSize(660, 460);
        setLocation(100, 100);
        setResizable(false);

///////////////////KLIENCI//////////

        // panel do zarządzania klientami
        p_kli.setLayout(null);

        // pole z peselem
        JLabel lab1 = new JLabel("pesel:");
        p_kli.add(lab1);
        lab1.setSize(100, 20);
        lab1.setLocation(40, 40);
        lab1.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_pesel);
        pole_pesel.setSize(200, 20);
        pole_pesel.setLocation(160, 40);

        // pole z imieniem
        JLabel lab2 = new JLabel("imię:");
        p_kli.add(lab2);
        lab2.setSize(100, 20);
        lab2.setLocation(40, 80);
        lab2.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_im);
        pole_im.setSize(200, 20);
        pole_im.setLocation(160, 80);

        // pole z nazwiskiem
        JLabel lab3 = new JLabel("nazwisko:");
        p_kli.add(lab3);
        lab3.setSize(100, 20);
        lab3.setLocation(40, 120);
        lab3.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_naz);
        pole_naz.setSize(200, 20);
        pole_naz.setLocation(160, 120);

        // pole z datą urodzenia
        JLabel lab4 = new JLabel("data urodzenia:");
        p_kli.add(lab4);
        lab4.setSize(100, 20);
        lab4.setLocation(40, 160);
        lab4.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_ur);
        pole_ur.setSize(200, 20);
        pole_ur.setLocation(160, 160);

        // pole z mailem
        JLabel lab5 = new JLabel("mail:");
        p_kli.add(lab5);
        lab5.setSize(100, 20);
        lab5.setLocation(40, 200);
        lab5.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_mail);
        pole_mail.setSize(200, 20);
        pole_mail.setLocation(160, 200);

        // pole z adresem
        JLabel lab6 = new JLabel("adres:");
        p_kli.add(lab6);
        lab6.setSize(100, 20);
        lab6.setLocation(40, 240);
        lab6.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_adr);
        pole_adr.setSize(200, 20);
        pole_adr.setLocation(160, 240);

        // pole z telefonem
        JLabel lab7 = new JLabel("telefon:");
        p_kli.add(lab7);
        lab7.setSize(100, 20);
        lab7.setLocation(40, 280);
        lab7.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_tel);
        pole_tel.setSize(200, 20);
        pole_tel.setLocation(160, 280);

        // przycisk do zapisu klienta
        p_kli.add(przyc_zapisz_kli);
        przyc_zapisz_kli.setSize(200, 20);
        przyc_zapisz_kli.setLocation(160, 320);
        przyc_zapisz_kli.addActionListener(akc_zap_kli);

        // przycisk do usunięcia klienta
        p_kli.add(przyc_usun_kli);
        przyc_usun_kli.setSize(200, 20);
        przyc_usun_kli.setLocation(400, 320);
        przyc_usun_kli.addActionListener(akc_usun_kli);

        // lista z klientami
        p_kli.add(sp_kli);
        sp_kli.setSize(200, 260);
        sp_kli.setLocation(400, 40);
        l_kli.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKlientów(l_kli);

        // panel z zakładkami
        tp.addTab("klienci", p_kli);
        tp.addTab("książki", p_ksi);
        tp.addTab("zamówienia", p_zam);
        getContentPane().add(tp, BorderLayout.CENTER);
        // pole na komentarze
        komunikat.setEditable(false);
        getContentPane().add(komunikat, BorderLayout.SOUTH);
        // pokazanie okna
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

  ///////////////KSIAZKI///////////////
        // panel do zarządzania ksiazkami
        p_ksi.setLayout(null);

        // pole z ISBN
        JLabel labelISBN = new JLabel("ISBN:");
        p_ksi.add(labelISBN);
        labelISBN.setSize(100, 20);
        labelISBN.setLocation(20, 20);
        labelISBN.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_isbn);
        pole_isbn.setSize(200, 20);
        pole_isbn.setLocation(110, 20);

        // pole z autor
        JLabel labautor = new JLabel("autor:");
        p_ksi.add(labautor);
        labautor.setSize(100, 20);
        labautor.setLocation(20, 60);
        labautor.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_autor);
        pole_autor.setSize(200, 20);
        pole_autor.setLocation(110, 60);

        // pole z tytul
        JLabel labtytul = new JLabel("tytul:");
        p_ksi.add(labtytul);
        labtytul.setSize(100, 20);
        labtytul.setLocation(20, 100);
        labtytul.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_tytul);
        pole_tytul.setSize(200, 20);
        pole_tytul.setLocation(110, 100);

        // pole z typ
        JLabel labtyp = new JLabel("typ:");
        p_ksi.add(labtyp);
        labtyp.setSize(100, 20);
        labtyp.setLocation(20, 140);
        labtyp.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(lista_typ);
        lista_typ.setSize(200, 20);
        lista_typ.setLocation(110, 140);

        for (typksiazki typksi: typksiazki.values()
        ) {
            lista_typ.addItem(typksi);
        }

        // pole z wydawnictwo
        JLabel labwydaw = new JLabel("wydawnictwo:");
        p_ksi.add(labwydaw);
        labwydaw.setSize(100, 20);
        labwydaw.setLocation(340, 20);
        labwydaw.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_wydawnictwo);
        pole_wydawnictwo.setSize(200, 20);
        pole_wydawnictwo.setLocation(440, 20);

        // pole z rok
        JLabel labrok = new JLabel("rok:");
        p_ksi.add(labrok);
        labrok.setSize(100, 20);
        labrok.setLocation(340, 60);
        labrok.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_rok);
        pole_rok.setSize(200, 20);
        pole_rok.setLocation(440, 60);

        // pole z cena
        JLabel labcena = new JLabel("cena:");
        p_ksi.add(labcena);
        labcena.setSize(100, 20);
        labcena.setLocation(340, 100);
        labcena.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_cena);
        pole_cena.setSize(200, 20);
        pole_cena.setLocation(440, 100);

        // pole nowa cena
        JLabel labnowacena = new JLabel(" zmień cenę:");
        p_ksi.add(labnowacena);
        labnowacena.setSize(100, 20);
        labnowacena.setLocation(340, 140);
        labnowacena.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_nowa_cena);
        pole_nowa_cena.setSize(100, 20);
        pole_nowa_cena.setLocation(440, 140);
        // przycisk do zapisu nowej ceny
        p_ksi.add(przyc_nowa_cena);
        przyc_nowa_cena.setSize(100, 20);
        przyc_nowa_cena.setLocation(540, 140);
        przyc_nowa_cena.addActionListener(akc_nowa_cena);

        // lista z ksiazkami
        p_ksi.add(sp_ksi);
        sp_ksi.setSize(620, 140);
        sp_ksi.setLocation(20, 180);
        l_ksi.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKsiazek(l_ksi);

        // przycisk do zapisu ksiazek
        p_ksi.add(przyc_zapisz_ksi);
        przyc_zapisz_ksi.setSize(200, 20);
        przyc_zapisz_ksi.setLocation(440, 340);
        przyc_zapisz_ksi.addActionListener(akc_zap_ksi);

        // przycisk do usunięcia ksiazek
        p_ksi.add(przyc_usun_ksi);
        przyc_usun_ksi.setSize(200, 20);
        przyc_usun_ksi.setLocation(110, 340);
        przyc_usun_ksi.addActionListener(akc_usun_ksi);



        // pole na komentarze
        komunikat.setEditable(false);
        getContentPane().add(komunikat, BorderLayout.SOUTH);
        // pokazanie okna
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

 ////////////////////ZAMOWIENIA/////////////
        // panel do zarządzania zamoweniami
        p_zam.setLayout(null);

        // lista z ksiązkami dla panelu zamowień
        p_zam.add(sp_ksi_zam);
        sp_ksi_zam.setSize(300, 110);
        sp_ksi_zam.setLocation(20, 40);
        lista_ksi_zam.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        AktualnaListaKsiazekZamow();

        // lista z klientami dla panelu zamowień
        p_zam.add(sp_kli_zam);
        sp_kli_zam.setSize(300, 110);
        sp_kli_zam.setLocation(20, 160);
        lista_kli_zam.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKlientówZamow();

        // pole data zamowiena
        JLabel labdata = new JLabel("data:");
        p_zam.add(labdata);
        labdata.setSize(120, 20);
        labdata.setLocation(340, 60);
        labdata.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(pole_kiedy);
        pole_kiedy.setSize(180, 20);
        pole_kiedy.setLocation(460, 60);


        // przycisk zrób zamówienie
        p_zam.add(przyc_zrob_zamowienie);
        przyc_zrob_zamowienie.setSize(300, 40);
        przyc_zrob_zamowienie.setLocation(340, 110);
        przyc_zrob_zamowienie.addActionListener(akc_zrob_zam);

        // lista zamówień dla panelu zamowień
        p_zam.add(sp_zam);
        sp_zam.setSize(300, 110);
        sp_zam.setLocation(340, 160);
        lista_zam.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaZamow();

        // pole status
        JLabel labstatus = new JLabel("status:");
        p_zam.add(labstatus);
        labstatus.setSize(120, 20);
        labstatus.setLocation(340, 290);
        labstatus.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(lista_status);
        lista_status.setSize(180, 20);
        lista_status.setLocation(460, 290);

        for (statusKsiazki status: statusKsiazki.values()
        ) {
            lista_status.addItem(status);
        }

        // przycisk do usunięcia zamówień
        p_zam.add(przyc_usun_zamowienia);
        przyc_usun_zamowienia.setSize(300, 20);
        przyc_usun_zamowienia.setLocation(20, 330);
        //przyc_usun_zamowienia.addActionListener(akc_usun_zam);

        // przycisk zmień status zamówienia
        p_zam.add(przyc_zmien_status);
        przyc_zmien_status.setSize(300, 20);
        przyc_zmien_status.setLocation(340, 330);
        przyc_zmien_status.addActionListener(akc_zmien_status);



    }
}


public class App {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        new Okno();
    }
}