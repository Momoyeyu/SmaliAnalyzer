import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.File;

public class DirectoryChooserExample {

    public static void main(String[] args) {
        // 创建JFileChooser实例
        JFileChooser fileChooser = new JFileChooser();

        // 设置文件选择器以使用目录模式
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // 打开文件选择器对话框
        int result = fileChooser.showOpenDialog(new JFrame());

        // 检查用户是否选择了目录
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取用户选择的目录
            File selectedDirectory = fileChooser.getSelectedFile();
            System.out.println("Selected directory: " + selectedDirectory.getAbsolutePath());
        }
    }
}