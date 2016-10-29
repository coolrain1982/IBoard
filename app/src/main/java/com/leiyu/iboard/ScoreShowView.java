package com.leiyu.iboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.leiyu.iboard.socket.SocketHandler;
import com.leiyu.iboard.draw.AShape;
import com.leiyu.iboard.draw.Curve;
import com.leiyu.iboard.draw.DrawList;
import com.leiyu.iboard.draw.IShape;
import com.leiyu.iboard.score.LoadScore;
import com.leiyu.iboard.transmission.Command;
import com.leiyu.iboard.transmission.ConnectToServer;
import com.leiyu.iboard.transmission.ConnectToServerException;
import com.leiyu.iboard.transmission.InterCmdQueue;
import com.leiyu.iboard.transmission.SerializeTool;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

/**
 * Created by leiyu on 2016/10/20.
 */

public class ScoreShowView extends View {

    private String scoreName = null;
    private int width, height = 0;
    private ScoreShowView self = null;
    private Map<String, DrawList> drawListMap = new HashMap<>();
    private final  Boolean drawListSyn = Boolean.FALSE;
    private Bitmap scoreBitmap = null;
    private boolean isInit = true;
    private AShape curShape = null;
    //iboard是否启动
    private boolean isBoardStart = false;
    //是否在做标记模式
    private boolean isComment = false;
    //后台画布的相关对象
    private Paint bitmapPaint;
    private Bitmap backBitmap;
    private Canvas backCanvas;
    //画图信息传递相关对象
    private long iboardID = 0;
    private String userName = "invalid";
    private Socket socket = null;
    private InterCmdQueue interCmdQueue;
    private SocketHandler socketHandler;
    private UpdateUIFromServer updateUIFromServer;


    public ScoreShowView(Context context, AttributeSet as) {
        super(context, as);
        self = this;
        //取view的长度和宽度
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        width = self.getWidth();
                        height = self.getHeight();

//                        backBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                        backCanvas = new Canvas(backBitmap);
                    }
                });
//        bitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void iboardStart() {
        //不能重复启动iboard
        if (isBoardStart) {
            return;
        }

        //连接服务器
        ConnectToServer connectToServer = new ConnectToServer();
        try {
            socket = connectToServer.connect();
        } catch (ConnectToServerException cte) {
            connectToServer.showFailedDialog();
            return;
        } catch (Exception e1) {
            return;
        }

        //
        iboardID = System.currentTimeMillis();

        //
        interCmdQueue = new InterCmdQueue();
        try {
            socketHandler = new SocketHandler(socket, interCmdQueue);
            socketHandler.listen();
        } catch (Exception e) {
            iboardEnd();
            return;
        }

        updateUIFromServer = new UpdateUIFromServer(interCmdQueue);
        updateUIFromServer.start();

        try {
            userName = getResources().getString(R.string.user_name);
        } catch (Exception e) {}

        interCmdQueue.addCmdOut(String.format("%04d%08d%s", Command.COMMAND_USERID,
                userName.length(), userName));

        isBoardStart = true;
    }

    /**
     * iboard停止
     */
    public void iboardEnd() {

        socketHandler.shutDown();
        socketHandler = null;

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (updateUIFromServer != null) {
            updateUIFromServer.finish();
        }

        interCmdQueue.clear();
        interCmdQueue = null;

        updateUIFromServer = null;

        isBoardStart = false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInit) {
            this.scoreBitmap = LoadScore.loadScore(getContext(), scoreName, width, height);
            isInit = false;
        }

        if (scoreBitmap != null) {
            canvas.drawBitmap(scoreBitmap, 0, 0, null);
        }

        if (isBoardStart) {
            if (drawListMap.containsKey(scoreName)) {
                //开始画记号
                List<IShape> drawPathList = getDrawList();
                if (drawPathList.size() > 0) {
                    canvas.save();

                    for (IShape drawPath : drawPathList) {
                        drawPath.draw(canvas);
                    }

                    canvas.restore();
                }
            }

//            if (backBitmap != null) {
//                canvas.save();
//                canvas.drawBitmap(backBitmap, 0, 0, bitmapPaint);
//                canvas.restore();
//            }

            if (curShape != null) {
                canvas.save();
                curShape.draw(canvas);
                canvas.restore();
            }
        }

    }

    public void showScore(String scoreName) {

        this.scoreName = scoreName;

        Bitmap bitmap = LoadScore.loadScore(getContext(), scoreName, width, height);

        if (bitmap != null) {

            this.scoreBitmap = bitmap;
            invalidate();
        }
    }

    /**
     *增加画图路径
     */
    public void addDrawList(IShape drawPath) {

        DrawList drawList = null;
        synchronized (drawListSyn) {
            if (!drawListMap.containsKey(scoreName)) {
                drawListMap.put(scoreName, new DrawList());
            }

            drawList = drawListMap.get(scoreName);
            drawList.add(drawPath);
        }
    }

    public List<IShape> getDrawList() {

        synchronized (drawListSyn) {
            if (drawListMap.containsKey(scoreName)) {
                return drawListMap.get(scoreName).getAllshape();
            }
        }

        return null;
    }

    /**
     * 获取画笔颜色
     * @return
     */
    public static int getColor() {
        return Color.RED;
    }

    public  static float getPenWidth() {
        return 4.0f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isBoardStart && MainActivity.role == 1) {
                    curShape = new Curve(0);
                    curShape.setIboardID(iboardID);
                    curShape.touchDown(x, y);
                    sendShapeToServer(Command.COMMAND_DRAWSHAPE_START,curShape);
                }
                break;
            case MotionEvent.ACTION_MOVE :
                if (isBoardStart && MainActivity.role == 1) {
                    curShape.touchMove(x, y);
                    curShape.setStatusMove();
                    sendShapeToServer(Command.COMMAND_DRAWSHAPE_MOVE, curShape);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isBoardStart && MainActivity.role == 1) {
                    //curShape.draw(backCanvas);
                    invalidate();
                    addDrawList(curShape);
                    curShape.setStatusEnd();
                    sendShapeToServer(Command.COMMAND_DRAWSHAPE_END, curShape);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void sendShapeToServer(int commandID, AShape shape) {
        //先序列化对象
        String objSerial = SerializeTool.object2String(shape);
        if (objSerial == null) {
            return;
        }

        //格式化
        interCmdQueue.addCmdOut(String.format("%04d%08d%s", commandID,
                objSerial.length(), objSerial));
    }

    class UpdateUIFromServer extends Thread {
        private InterCmdQueue interCmdQueue_inner;
        private boolean isFinish = false;
        UpdateUIFromServer(InterCmdQueue icq) {
            interCmdQueue_inner = icq;
        }

        public void finish() {
            isFinish = true;
        }

        @Override
        public void run() {
            while (!isFinish) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //从队列中取出消息
                if (interCmdQueue_inner == null) {
                    continue;
                }

                Object commandObject = interCmdQueue_inner.getCmdIn();
                if (commandObject == null) {
                    continue;
                }
                if (!Command.class.isInstance(commandObject)) {
                    continue;
                }

                Command command = (Command) commandObject;

                AShape ashape = SerializeTool.getObjectFromString(command.getCommand(), AShape.class);

                if(ashape == null) {
                    continue;
                }

                switch (command.getCommandId()) {
                    case Command.COMMAND_DRAWSHAPE_START:
                        break;
                    case Command.COMMAND_DRAWSHAPE_MOVE:
                        curShape = ashape;
                        ContextInfo.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                        break;
                    case Command.COMMAND_DRAWSHAPE_END:
                        ContextInfo.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });

                        addDrawList(curShape);

                        break;
                    default:
                        break;
                }
            }
        }
    }
}
