package org.projeto;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaBazar extends JFrame {

    public TelaBazar() {
        setTitle("Painel do Bazar");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        Color fundo = new Color(180, 255, 180);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(new EmptyBorder(20,20,20,20));
        setContentPane(painelPrincipal);

        // ===== TOPO =====
        JLabel titulo = new JLabel("Painel do Bazar");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        String nome = SessaoUsuario.getInstance().getNomeUsuario();

        JLabel usuarioLabel =
                new JLabel("Bem-vindo, " + nome,
                        SwingConstants.CENTER);

        usuarioLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel topo = new JPanel(new GridLayout(2,1));
        topo.setBackground(fundo);
        topo.add(titulo);
        topo.add(usuarioLabel);

        painelPrincipal.add(topo, BorderLayout.NORTH);

        // ===== DASHBOARD =====
        JPanel dashboard = new JPanel(new GridLayout(1,3,10,10));
        dashboard.setBackground(fundo);

        dashboard.add(criarCard("Produtos", "12"));
        dashboard.add(criarCard("Pedidos", "4"));
        dashboard.add(criarCard("Vendas", "38"));

        painelPrincipal.add(dashboard, BorderLayout.CENTER);

        // ===== BOTÕES =====
        JPanel botoes = new JPanel(new GridLayout(3,2,15,15));
        botoes.setBackground(fundo);
        botoes.setBorder(new EmptyBorder(30,0,0,0));

        JButton addBtn = new JButton("Adicionar Produto");
        JButton editBtn = new JButton("Editar Produto");
        JButton pedidosBtn = new JButton("Pedidos");
        JButton excluirBtn = new JButton("Excluir Produto");
        JButton logoutBtn = new JButton("Logout");
        JButton voltarBtn = new JButton("Voltar");

        addBtn.addActionListener(e ->
                new TelaAdicionarProduto().setVisible(true));

        editBtn.addActionListener(e ->
                new TelaEditarProduto().setVisible(true));

        pedidosBtn.addActionListener(e ->
                new TelaPedidos().setVisible(true));

        excluirBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Tela de exclusão ainda será criada"));

        logoutBtn.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja sair?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if(op == JOptionPane.YES_OPTION){
                SessaoUsuario.getInstance().encerrarSessao();
                new TelaLogin().setVisible(true);
                dispose();
            }
        });

        voltarBtn.addActionListener(e -> {
            new TelaInicial().setVisible(true);
            dispose();
        });

        botoes.add(addBtn);
        botoes.add(editBtn);
        botoes.add(pedidosBtn);
        botoes.add(excluirBtn);
        botoes.add(logoutBtn);
        botoes.add(voltarBtn);

        painelPrincipal.add(botoes, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel criarCard(String titulo, String valor){
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.setBackground(Color.white);

        JLabel t = new JLabel(titulo, SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel v = new JLabel(valor, SwingConstants.CENTER);
        v.setFont(new Font("Arial", Font.BOLD, 26));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaBazar::new);
    }
}


