package ナイトツアー;
import java.awt.Color; //色の表現操作
import java.awt.Cursor; //マウスカーソルの操作
import java.awt.Font; //ボタンのテキスト
import java.awt.Graphics; //グラフィック操作ここではチェス盤とその上の要素を描画
import java.awt.event.ActionEvent; //ボタンクリック処理
import java.awt.event.ActionListener;//
import java.awt.event.MouseAdapter; //マウスクリックの処理
import java.awt.event.MouseEvent;//

import javax.swing.JButton;//ボタンクリックアクション
import javax.swing.JFrame;//ウィンドウ表示
import javax.swing.JOptionPane;//エラーを表示するために使用している
import javax.swing.SwingUtilities;//

//JFrameを継承したKnightTourGameクラスの定義
public class KnightTourGame extends JFrame {
    // チェスボードの状態を表す2次元配列
    private int[][] chessboard;
    // 現在の騎士の移動
    private int currentMove;
    private int currentRow;
    private int currentCol;
    // 初期位置が選択されたかどうかと、ゲームが開始したかどうかを示すフラグ
    private boolean initialPositionSelected;
    private boolean gameStarted;
    // チェスボードのサイズ
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 50;
    private static final int WINDOW_WIDTH = BOARD_SIZE * CELL_SIZE + 100;
    private static final int WINDOW_HEIGHT = BOARD_SIZE * CELL_SIZE + 100;
    // 騎士の移動パターン
    private final int[] rowMoves = {2, 1, -1, -2, -2, -1, 1, 2};
    private final int[] colMoves = {1, 2, 2, 1, -1, -2, -2, -1};
    // コンストラクタ
    public KnightTourGame() {
        // チェスボードの初期化
        chessboard = new int[BOARD_SIZE][BOARD_SIZE];
        currentMove = 1;
        currentRow = 0;
        currentCol = 0;
        initialPositionSelected = false;
        gameStarted = false;
        // ゲームウィンドウの設定
        setTitle("Knight's Tour");// タイトルバーのタイトル
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);// ウィンドウのサイズ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じたときの動作

        // "Start Game" ボタンの設定
        JButton startButton = new JButton("Start Game"); // ボタンのテキスト
        startButton.setBounds(10, BOARD_SIZE * CELL_SIZE, 200, 60);  // 背景が赤のボタンを大きく表示
        startButton.setBackground(Color.RED);  // ボタンの背景色を赤に設定
        startButton.setForeground(Color.BLACK);  // ボタンの文字色を黒に設定
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));  // ボタンのフォントサイズを大きく設定

        // "Start Game" ボタンにアクションリスナーを追加
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ゲームが開始されたことを示すフラグを立てる
                gameStarted = true;
                // "Start Game" ボタンを非表示にし、ゲームを開始
                startButton.setVisible(false);
                // ゲーム画面を再描画
                repaint();
            }
        });

        // ボタンをウィンドウに追加
        add(startButton);

        // レイアウトマネージャを無効にする（ボタンの位置指定にレイアウトマネージャを使用しない）
        setLayout(null);

        // マウスリスナーを追加
        addMouseListener(new ChessboardMouseListener());

        // ゲームウィンドウを表示
        setVisible(true);
    }

    // チェスボード上の指定された位置 (row, col) に移動可能かどうかを判定するメソッド
    public boolean isMoveValid(int row, int col) {
        // 現在の位置から指定した位置 (row, col) への移動が可能かを判定するため、
        // 別のisMoveValidメソッドを呼び出します。
        return isMoveValid(currentRow, currentCol, row, col);
    }

    // 現在の位置から指定した位置 (nextRow, nextCol) への移動が可能かどうかを判定するメソッド
    public boolean isMoveValid(int currentRow, int currentCol, int nextRow, int nextCol) {
        // 騎士の移動可能なすべての方向を示す配列を定義
        int[] rowMoves = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] colMoves = {1, 2, 2, 1, -1, -2, -2, -1};

        // 8つの移動方向それぞれについて、指定された位置 (nextRow, nextCol) が移動可能かを検証
        for (int i = 0; i < 8; i++) {
            int possibleRow = currentRow + rowMoves[i];  // 次の行の位置
            int possibleCol = currentCol + colMoves[i];  // 次の列の位置
            if (possibleRow == nextRow && possibleCol == nextCol) {
                return true;  // 指定位置への移動が可能
            }
        }
        return false;  // 指定位置への移動が不可能
    }

    // 指定された位置 (row, col) から移動可能な位置の数を数えるメソッド
    public int countPossibleMoves(int row, int col) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int newRow = row + rowMoves[i];  // 次の行の位置
            int newCol = col + colMoves[i];  // 次の列の位置
            if (isMoveValid(newRow, newCol) && chessboard[newRow][newCol] == 0) {
                count++;  // 移動可能な位置の数を増やす
            }
        }
        return count;  // 移動可能な位置の数を返す
    }
    class ChessboardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;  // マウスクリックのX座標から列を取得
            int row = e.getY() / CELL_SIZE;  // マウスクリックのY座標から行を取得

            if (!gameStarted) {
                return; // ゲームがまだ開始されていない場合は何もしない
            }

            if (!initialPositionSelected) {
                // 初期位置を選択する
                currentRow = row;
                currentCol = col;
                chessboard[currentRow][currentCol] = 1;  // 選択した位置を 1 でマーク
                initialPositionSelected = true;
            } else if (isMoveValid(row, col) && chessboard[row][col] == 0) {
                // 選択した位置 (row, col) が移動可能で、かつ空いている場合
                if (isValidMove(currentRow, currentCol, row, col)) {
                    currentMove++;  // 移動回数を増やす
                    chessboard[row][col] = currentMove;  // 選択した位置に移動回数を設定
                    currentRow = row;
                    currentCol = col;
                    repaint();  // チェスボードを再描画
                } else {
                    JOptionPane.showMessageDialog(KnightTourGame.this, "Invalid move. Please try again.");
                    // 無効な移動の場合、エラーダイアログを表示
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;  // マウスが入ったセルの列を取得
            int row = e.getY() / CELL_SIZE;  // マウスが入ったセルの行を取得

            if (!gameStarted || !initialPositionSelected) {
                return;  // ゲームが開始されていないか、初期位置が選択されていない場合は何もしない
            }

            if (isMoveValid(row, col) && chessboard[row][col] == 0) {
                if (isValidMove(currentRow, currentCol, row, col)) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));  // カーソルを手の形に変更
                    chessboard[row][col] = -1; // 移動可能な位置としてマークする（-1）
                    repaint();
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  // カーソルを通常の形状に変更
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;  // マウスが出たセルの列を取得
            int row = e.getY() / CELL_SIZE;  // マウスが出たセルの行を取得

            if (isMoveValid(row, col) && chessboard[row][col] == -1) {
                chessboard[row][col] = 0; // 移動可能な位置のマークを削除
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  // カーソルを通常の形状に変更
                repaint();
            }
        }
    }
    public boolean isValidMove(int currentRow, int currentCol, int nextRow, int nextCol) {
        int[] rowMoves = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] colMoves = {1, 2, 2, 1, -1, -2, -2, -1};
        for (int i = 0; i < 8; i++) {
            int possibleRow = currentRow + rowMoves[i];
            int possibleCol = currentCol + colMoves[i];
            if (possibleRow == nextRow && possibleCol == nextCol) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        if (!gameStarted) {
            // Display a message or instructions on the start screen.
            g.setColor(Color.BLACK);
            g.drawString("Click 'Start Game' to begin", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 + 50);
        } else {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    int x = col * CELL_SIZE;
                    int y = row * CELL_SIZE;

                    // チェス盤のセルを描画
                    if ((row + col) % 2 == 0) {
                        g.setColor(Color.WHITE);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                    // 移動可能なセルを〇で可視化
                    if (chessboard[row][col] > 0) {
                        g.setColor(Color.RED);
                        g.drawString(String.valueOf(chessboard[row][col]), x + 20, y + 30);
                    } else if (chessboard[row][col] == -1) {
                        g.setColor(Color.RED);
                        g.drawOval(x + 20, y + 20, 10, 10);
                    } else if (chessboard[row][col] == 0 && isMoveValid(currentRow, currentCol, row, col)) {
                        g.setColor(Color.RED);
                        g.drawString("○", x + 20, y + 30);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KnightTourGame();
        });
    }
}

