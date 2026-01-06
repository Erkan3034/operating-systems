import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class RAGDeadlockAnalyzer extends JFrame {
    
    // Graf yapısı
    private Map<String, List<String>> graph;
    private Set<String> processes;
    private Set<String> resources; 
    
    // Koordinatlar
    private Map<String, Point> nodePositions;
    
    public RAGDeadlockAnalyzer() {
        // Graf yapısını başlat
        graph = new HashMap<>();
        processes = new HashSet<>();
        resources = new HashSet<>();
        nodePositions = new HashMap<>();
        
        // İşlemleri ekle
        processes.add("P1");
        processes.add("P2");
        processes.add("P3");
        
        // Kaynakları ekle
        resources.add("R1");
        resources.add("R2");
        resources.add("R3");
        
        // Graf ilişkilerini oluştur
        buildRAG();
        
        // Koordinatları ayarla
        setupNodePositions();
        
        // Pencere ayarları
        setTitle("Resource Allocation Graph (RAG) - Deadlock Analizi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void buildRAG() {
        // Senaryoya göre RAG oluştur
        
        // P1, R1'i tutuyor (R1 → P1)
        addEdge("R1", "P1");
        
        // P1, R2'yi istiyor (P1 → R2)
        addEdge("P1", "R2");
        
        // P2, R2'yi tutuyor (R2 → P2)
        addEdge("R2", "P2");
        
        // P2, R3'ü istiyor (P2 → R3)
        addEdge("P2", "R3");
        
        // P3, R3'ü tutuyor (R3 → P3)
        addEdge("R3", "P3");
        
        // P3, R1'i istiyor (P3 → R1)
        addEdge("P3", "R1");
    }
    
    private void addEdge(String from, String to) {
        graph.putIfAbsent(from, new ArrayList<>());
        graph.get(from).add(to);
    }
    
    private void setupNodePositions() {
        int centerX = 400;
        int centerY = 300;
        int radius = 150;
        
        // İşlemleri daire üzerine yerleştir (sol taraf)
        List<String> procList = new ArrayList<>(processes);
        for (int i = 0; i < procList.size(); i++) {
            double angle = Math.PI + (i * 2 * Math.PI / 3);
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            nodePositions.put(procList.get(i), new Point(x, y));
        }
        
        // Kaynakları daire üzerine yerleştir (sağ taraf)
        List<String> resList = new ArrayList<>(resources);
        for (int i = 0; i < resList.size(); i++) {
            double angle = i * 2 * Math.PI / 3;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            nodePositions.put(resList.get(i), new Point(x, y));
        }
    }
    
    // Döngü tespiti (Deadlock kontrolü)
    private boolean detectCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        
        for (String node : graph.keySet()) {
            if (detectCycleUtil(node, visited, recStack)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean detectCycleUtil(String node, Set<String> visited, Set<String> recStack) {
        if (recStack.contains(node)) {
            return true;
        }
        
        if (visited.contains(node)) {
            return false;
        }
        
        visited.add(node);
        recStack.add(node);
        
        List<String> neighbors = graph.get(node);
        if (neighbors != null) {
            for (String neighbor : neighbors) {
                if (detectCycleUtil(neighbor, visited, recStack)) {
                    return true;
                }
            }
        }
        
        recStack.remove(node);
        return false;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Kenarları çiz
        drawEdges(g2d);
        
        // Düğümleri çiz
        drawNodes(g2d);
        
        // Deadlock sonucunu göster
        drawDeadlockResult(g2d);
    }
    
    private void drawEdges(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));
        
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String from = entry.getKey();
            Point fromPos = nodePositions.get(from);
            
            for (String to : entry.getValue()) {
                Point toPos = nodePositions.get(to);
                
                // Ok rengini belirle (Atama kenarı mavi, İstek kenarı kırmızı)
                if (resources.contains(from)) {
                    g2d.setColor(Color.BLUE); // R → P (Atama kenarı)
                } else {
                    g2d.setColor(Color.RED); // P → R (İstek kenarı)
                }
                
                // Ok çiz
                drawArrow(g2d, fromPos.x, fromPos.y, toPos.x, toPos.y);
            }
        }
    }
    
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Çizgi çiz
        g2d.drawLine(x1, y1, x2, y2);
        
        // Ok başı çiz
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;
        
        int[] xPoints = {
            x2,
            x2 - (int)(arrowSize * Math.cos(angle - Math.PI / 6)),
            x2 - (int)(arrowSize * Math.cos(angle + Math.PI / 6))
        };
        
        int[] yPoints = {
            y2,
            y2 - (int)(arrowSize * Math.sin(angle - Math.PI / 6)),
            y2 - (int)(arrowSize * Math.sin(angle + Math.PI / 6))
        };
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawNodes(Graphics2D g2d) {
        int nodeSize = 60;
        
        // İşlemleri çiz (Daire)
        for (String process : processes) {
            Point pos = nodePositions.get(process);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(pos.x - nodeSize/2, pos.y - nodeSize/2, nodeSize, nodeSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - nodeSize/2, pos.y - nodeSize/2, nodeSize, nodeSize);
            
            // Metin
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(process);
            g2d.drawString(process, pos.x - textWidth/2, pos.y + 5);
        }
        
        // Kaynakları çiz (Kare)
        for (String resource : resources) {
            Point pos = nodePositions.get(resource);
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(pos.x - nodeSize/2, pos.y - nodeSize/2, nodeSize, nodeSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(pos.x - nodeSize/2, pos.y - nodeSize/2, nodeSize, nodeSize);
            
            // Metin
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(resource);
            g2d.drawString(resource, pos.x - textWidth/2, pos.y + 5);
        }
    }
    
    private void drawDeadlockResult(Graphics2D g2d) {
        boolean hasDeadlock = detectCycle();
        
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        
        String result;
        if (hasDeadlock) {
            g2d.setColor(Color.RED);
            result = "DEADLOCK TESPİT EDİLDİ!";
        } else {
            g2d.setColor(Color.GREEN);
            result = "DEADLOCK YOKTUR";
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(result);
        g2d.drawString(result, (getWidth() - textWidth) / 2, 80);
        
        // Açıklama
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.BLACK);
        String legend1 = "Mavi oklar: Atama kenarı (R → P)";
        String legend2 = "Kırmızı oklar: İstek kenarı (P → R)";
        g2d.drawString(legend1, 50, getHeight() - 80);
        g2d.drawString(legend2, 50, getHeight() - 60);
    }
    
    public void analyzeAndPrint() {
        System.out.println("=== Resource Allocation Graph (RAG) Analizi ===\n");
        
        System.out.println("İşlemler: " + processes);
        System.out.println("Kaynaklar: " + resources);
        System.out.println();
        
        System.out.println("Graf İlişkileri:");
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            for (String to : entry.getValue()) {
                String type = resources.contains(entry.getKey()) ? "Atama" : "İstek";
                System.out.println("  " + entry.getKey() + " → " + to + " (" + type + " kenarı)");
            }
        }
        System.out.println();
        
        boolean hasDeadlock = detectCycle();
        System.out.println("Döngü Analizi: " + (hasDeadlock ? "Döngü bulundu" : "Döngü bulunamadı"));
        System.out.println();
        
        if (hasDeadlock) {
            System.out.println("SONUÇ: DEADLOCK TESPİT EDİLDİ!");
            System.out.println("Açıklama: P1 → R2 → P2 → R3 → P3 → R1 → P1 döngüsü var.");
        } else {
            System.out.println("SONUÇ: DEADLOCK YOKTUR");
        }
        
        System.out.println("\n===========================================");
    }
    
    public static void main(String[] args) {
        RAGDeadlockAnalyzer analyzer = new RAGDeadlockAnalyzer();
        
        // Konsol çıktısı
        analyzer.analyzeAndPrint();
        
        // Grafik gösterimi
        SwingUtilities.invokeLater(() -> {
            analyzer.setVisible(true);
        });
    }
}