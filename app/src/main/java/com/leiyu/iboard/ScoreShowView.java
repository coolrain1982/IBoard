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

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private long receAshapeTime = 0;


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
        receAshapeTime = 0;

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
//        String objSerial = SerializeTool.object2String(shape);
//        if (objSerial == null) {
//            return;
//        }

        StringBuffer sb = new StringBuffer();
        sb.append(shape.getTime());

        //先加第一个点
        if(shape.getPoints().size() > 0) {
            sb.append("||");
            sb.append(shape.getPoints().get(0)[0]);
            sb.append(",");
            sb.append(shape.getPoints().get(0)[1]);
        }

        //加最后一个点
        if(shape.getPoints().size() > 1) {
            sb.append("||");
            sb.append(shape.getPoints().get(shape.getPoints().size() - 1)[0]);
            sb.append(",");
            sb.append(shape.getPoints().get(shape.getPoints().size() - 1)[1]);
        }

//        Iterator<float[]> iter = shape.getPoints().iterator();
//        while(iter.hasNext())  {
//            float[] points = iter.next();
//            sb.append("||");
//            sb.append(points[0]);
//            sb.append(",");
//            sb.append(points[1]);
//        }

        //格式化
        interCmdQueue.addCmdOut(String.format("%04d%08d%s", commandID,
                sb.length(), sb));
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
                    Thread.sleep(1);
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

                String[] cmdArray = command.getCommand().split("\\|\\|");
                //第一个为时间戳
                if (cmdArray.length < 2) {
                    continue;
                }

                long curAshapeTime = 0;
                try {
                    curAshapeTime = Long.parseLong(cmdArray[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                List<float[]> points = new ArrayList<>();
                for(int i = 1; i < cmdArray.length; i ++) {

                    String[] pointArr = cmdArray[i].split(",") ;
                    if (pointArr.length == 2) {
                        try {
                            points.add(new float[]{Float.parseFloat(pointArr[0]),
                                    Float.parseFloat(pointArr[1])});
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }

                }

                if (points.size() == 0) {
                    continue;
                }

                switch (command.getCommandId()) {
                    case Command.COMMAND_DRAWSHAPE_START:
                        if (curAshapeTime != receAshapeTime) {
                            curShape = new Curve(0);
                            receAshapeTime = curAshapeTime;
                            Iterator<float[]> iterator1 = points.iterator();
                            if (iterator1.hasNext()) {
                                float[] point = iterator1.next();
                                curShape.touchDown(point[0], point[1]);
                            }
                        }
                        break;
                    case Command.COMMAND_DRAWSHAPE_MOVE:

                        if (curAshapeTime != receAshapeTime) {
                            curShape = new Curve(0);
                            receAshapeTime = curAshapeTime;
                            Iterator<float[]> iterator1 = points.iterator();
                            if (iterator1.hasNext()) {
                                float[] point = iterator1.next();
                                curShape.touchDown(point[0], point[1]);
                            }

                            while (iterator1.hasNext()) {
                                try {
                                    float[] point = iterator1.next();
                                    curShape.touchMove(point[0], point[1]);
                                } catch ( Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else {
                            if(curShape != null) {
                                float[] point = points.get(points.size() - 1);
                                curShape.touchMove(point[0], point[1]);
                            }
                        }

                        ContextInfo.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                        break;
                    case Command.COMMAND_DRAWSHAPE_END:

                        if (receAshapeTime == curAshapeTime && curShape != null) {
                            addDrawList(curShape);
                        }

                        break;
                    default:
                        break;
                }
            }
        }
    }
}
