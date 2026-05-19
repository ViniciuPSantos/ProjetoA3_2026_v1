package org.projeto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaInicial extends JFrame {

    public TelaInicial() {
        setTitle("EcoBazar - Tela Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);

        Color fundo = new Color(180, 255, 180, 237);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15,15));
        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        //logo
        ImageIcon logo = new ImageIcon("src/main/resources/logo.png");

        Image img = logo.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        JLabel logoLabel = new JLabel(new ImageIcon(img));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);


        //Titulo
        JLabel tituloLabel = new JLabel("EcoBazar", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel subtitulo = new JLabel("ODS 12 - Consumo e Produção Responsáveis.", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 16));

        JPanel topo = new JPanel(new GridLayout(3,1));
        topo.setBackground(fundo);

        topo.add(logoLabel);
        topo.add(tituloLabel);
        topo.add(subtitulo);

        //dashboard
        JPanel dashboard = new JPanel(new GridLayout(1,3,15,15));

        dashboard.setBackground(fundo);

        dashboard.add(criarCard("Produtos", "120"));
        dashboard.add(criarCard("Usúarios", "45"));
        dashboard.add(criarCard("Produtos", "30"));


        //descrição
        JTextArea descricao = new JTextArea("EcoBazar é uma plataforma de consumo consciente\n" +
                "que promove reutilização de roupas e produtos,\n" +
                "reduzindo desperdício e incentivando a economia circular.\n\n" +
                "Nosso projeto está alinhado ao ODS 12 da ONU:\n" +
                "Consumo e Produção Responsáveis.");

        descricao.setEditable(false);
        descricao.setFont(new Font("Arial", Font.PLAIN, 16));
        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setBackground(fundo);


        //botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));

        botoes.setBackground(fundo);

        JButton loginButton = new JButton("Login");

        JButton cadastrarButton = new JButton("Cadastrar-se");

        JButton navegarButton = new JButton("Explorar catálogo");

        JButton sobreButton = new JButton("Sobre");


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a TelaLogin
                new TelaLogin().setVisible(true);
                dispose();
            }
        });


        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a TelaCadastro
                new TelaCadastro().setVisible(true);
                dispose();
            }
        });


        navegarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria e exibe a TelaCatalogo
                new TelaCatalogo().setVisible(true);
                dispose();
            }
        });

        sobreButton.addActionListener(e -> JOptionPane.showMessageDialog(this,"EcoBazar promove consumo consciente\n" +
                        "e reutilização de produtos.\n\n" +
                        "Projeto alinhado ao ODS 12.",
                "Sobre o Projeto",
                JOptionPane.INFORMATION_MESSAGE ));

        botoes.add(loginButton);
        botoes.add(cadastrarButton);
        botoes.add(navegarButton);
        botoes.add(sobreButton);

        //centro
        JPanel centro = new JPanel(new BorderLayout(15,15));
        centro.setBackground(fundo);

        centro.add(dashboard, BorderLayout.NORTH);
        centro.add(descricao, BorderLayout.CENTER);

        painelPrincipal.add(topo, BorderLayout.NORTH);
        painelPrincipal.add(centro, BorderLayout.CENTER);
        painelPrincipal.add(botoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        setVisible(true);
    }

    public JPanel criarCard(String titulo, String valor){
        JPanel card = new JPanel(new BorderLayout());

        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel t = new JLabel(titulo, SwingConstants.CENTER);

        JLabel v = new JLabel(valor, SwingConstants.CENTER);

        v.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial());
    }
}

