package org.projeto;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaCatalogo extends JFrame {

    private JPanel produtosPanel;
    private JScrollPane scrollPane;
    private List<ProdutoCatalogo> listaDeProdutos;
    private List<ProdutoCatalogo> listaOriginal;

    private JButton voltarButton;
    private JButton carrinhoButton;
    private JButton pedidosButton;

    private JTextField buscaField;


    public TelaCatalogo() {
        setTitle("Catálogo de Produtos - EcoBazar");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        Color fundo = new Color(180, 255, 180, 237);
        getContentPane().setBackground(fundo);
        setLayout(new BorderLayout());

        //topo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(fundo);
        topPanel.setBorder(new EmptyBorder(15, 20, 15,20));

        JLabel titulo = new JLabel("Catálogo EcoBazar");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitulo = new JLabel("Moda sustentável e consumo consciente");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel textoTopo = new JPanel(new GridLayout(2,1));
        textoTopo.setOpaque(false);
        textoTopo.add(titulo);
        textoTopo.add(subtitulo);

        JPanel botoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoesTopo.setOpaque(false);

        buscaField = new JTextField(15);
        buscaField.addActionListener(e -> filtrarProdutos());

        pedidosButton = new JButton("Meus Pedidos");
        pedidosButton.addActionListener(e -> {
            // Verifica se o usuário está logado antes de abrir a tela de pedidos
            if (SessaoUsuario.getInstance().isUsuarioLogado()) {
                TelaPedidosCliente telaPedidos = new TelaPedidosCliente();
                telaPedidos.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Você precisa estar logado para ver seus pedidos.",
                        "Acesso Negado",
                        JOptionPane.WARNING_MESSAGE);
                // Opcional: abrir tela de login
                new TelaLogin().setVisible(true);
            }
        });


        carrinhoButton = new JButton("Carrinho");
        carrinhoButton.addActionListener(e -> {
            TelaCarrinho telaCarrinho = new TelaCarrinho();
            telaCarrinho.setVisible(true);
        });

        voltarButton = new JButton("Voltar");
        voltarButton.addActionListener(e -> {
            new TelaInicial().setVisible(true);
            dispose();
        });

        botoesTopo.add(new JLabel("Buscar:"));
        botoesTopo.add(buscaField);
        botoesTopo.add(pedidosButton);
        botoesTopo.add(carrinhoButton);
        botoesTopo.add(voltarButton);

        topPanel.add(textoTopo, BorderLayout.WEST);
        topPanel.add(botoesTopo, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //catálogo

        produtosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20,20));
        produtosPanel.setBackground(fundo);
        produtosPanel.setBorder(new EmptyBorder(20,20,20,20));

        scrollPane = new JScrollPane(produtosPanel);
        scrollPane.getViewport().setBackground(fundo);

        add(scrollPane, BorderLayout.CENTER);

        listaDeProdutos = new ArrayList<>();
        listaOriginal = new ArrayList<>();

        carregarProdutosDoBanco();
        exibirProdutos();

        setVisible(true);
    }

    private void filtrarProdutos() {
        String termo =
                buscaField.getText().toLowerCase().trim();

        listaDeProdutos.clear();

        for (ProdutoCatalogo p : listaOriginal) {
            if (p.getNome().toLowerCase().contains(termo)) {
                listaDeProdutos.add(p);
            }
        }

        exibirProdutos();
    }

    private void carregarProdutosDoBanco() {
        String sql =
                "SELECT id, nome, valor, imagens1_path FROM produtos";

        DBConnector db = new DBConnector();

        try (
                Connection conn = db.conectar();
                PreparedStatement pstmt =
                        conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {
            listaDeProdutos.clear();

            while (rs.next()) {
                ProdutoCatalogo p =
                        new ProdutoCatalogo(
                                rs.getInt("id"),
                                rs.getString("nome"),
                                rs.getDouble("valor"),
                                rs.getString("imagens1_path")
                        );

                listaDeProdutos.add(p);
            }

            listaOriginal =
                    new ArrayList<>(listaDeProdutos);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar produtos: "
                            + e.getMessage()
            );
        }
    }

    private void exibirProdutos() {
        produtosPanel.removeAll();

        for (ProdutoCatalogo produto : listaDeProdutos) {

            JPanel produtoPanel = new JPanel();
            produtoPanel.setLayout(
                    new BoxLayout(
                            produtoPanel,
                            BoxLayout.Y_AXIS
                    )
            );

            produtoPanel.setPreferredSize(
                    new Dimension(200, 300));

            produtoPanel.setBackground(Color.WHITE);

            produtoPanel.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    new Color(200,200,200),
                                    1,
                                    true
                            ),
                            new EmptyBorder(
                                    15,15,15,15
                            )
                    )
            );

            JLabel nomeLabel =
                    new JLabel(
                            "<html><body style='width:140px; text-align:center;'>"
                                    + produto.getNome()
                                    + "</body></html>"
                    );

            nomeLabel.setAlignmentX(
                    Component.CENTER_ALIGNMENT);

            nomeLabel.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            14
                    )
            );

            JLabel imagemLabel =
                    new JLabel(
                            carregarImagem(
                                    produto.getImagemPath()
                            )
                    );

            imagemLabel.setAlignmentX(
                    Component.CENTER_ALIGNMENT);

            JLabel valorLabel =
                    new JLabel(
                            "R$ "
                                    + String.format(
                                    "%.2f",
                                    produto.getValor()
                            )
                    );

            valorLabel.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            15
                    )
            );

            valorLabel.setAlignmentX(
                    Component.CENTER_ALIGNMENT);

            JButton addButton =
                    new JButton("🛒 Adicionar");

            addButton.setBackground(
                    new Color(34,139,34));

            addButton.setForeground(
                    Color.WHITE);

            addButton.setFocusPainted(false);

            addButton.setAlignmentX(
                    Component.CENTER_ALIGNMENT);

            addButton.addActionListener(e -> {
                if (SessaoUsuario.getInstance()
                        .isUsuarioLogado()) {

                    new TelaSelecaoTamanho(
                            produto.getId(),
                            produto.getNome(),
                            produto.getValor()
                    ).setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Faça login para comprar."
                    );
                }
            });

            produtoPanel.add(nomeLabel);
            produtoPanel.add(Box.createVerticalStrut(10));
            produtoPanel.add(imagemLabel);
            produtoPanel.add(Box.createVerticalStrut(10));
            produtoPanel.add(valorLabel);
            produtoPanel.add(Box.createVerticalStrut(15));
            produtoPanel.add(addButton);

            produtosPanel.add(produtoPanel);
        }

        produtosPanel.revalidate();
        produtosPanel.repaint();
    }

    private ImageIcon carregarImagem(String caminhoRelativo) {
        ImageIcon icon = null;

        if (caminhoRelativo != null &&
                !caminhoRelativo.isEmpty()) {

            File img = new File(caminhoRelativo);

            if (img.exists()) {
                try {
                    Image image =
                            new ImageIcon(
                                    img.toURI().toURL()
                            ).getImage()
                                    .getScaledInstance(
                                            150,
                                            150,
                                            Image.SCALE_SMOOTH
                                    );

                    icon =
                            new ImageIcon(image);

                } catch (Exception ignored) {}
            }
        }

        if (icon == null) {
            BufferedImage img =
                    new BufferedImage(
                            150,
                            150,
                            BufferedImage.TYPE_INT_RGB
                    );

            Graphics2D g =
                    img.createGraphics();

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0,0,150,150);

            g.setColor(Color.DARK_GRAY);
            g.drawString("Sem imagem", 40, 75);

            g.dispose();

            icon =
                    new ImageIcon(img);
        }

        return icon;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                TelaCatalogo::new
        );
    }
}

class ProdutoCatalogo {

    private int id;
    private String nome;
    private double valor;
    private String imagemPath;

    public ProdutoCatalogo(
            int id,
            String nome,
            double valor,
            String imagemPath
    ) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.imagemPath = imagemPath;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getValor() {
        return valor;
    }

    public String getImagemPath() {
        return imagemPath;
    }
}
