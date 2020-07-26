package ru.ekbapp.intexartwoker.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private SparseArray<Path> paths;
    Paint p;
    public DrawView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }

    private void setupDrawing() {
        paths = new SparseArray<>();
        p = new Paint();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLUE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(8);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }
    public Bitmap saveSignature(){
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }
    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        return bmp;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        p.setColor(Color.RED);
        // толщина линии = 10
        p.setStrokeWidth(15);

        canvas.drawRect(0, 150, 0, 0, p);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        for (int i=0; i<paths.size(); i++) {
            canvas.drawPath(paths.valueAt(i), drawPaint);
        }
    }
    public void clear() {
        for (int i=0; i<paths.size(); i++) {
            paths.get(i).reset();
        }
        paths.clear();
        drawPaint.setColor(Color.WHITE);
        drawCanvas.drawPaint(drawPaint);
        drawPaint.setColor(Color.BLUE);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);


        Path path;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                path = new Path();
                path.moveTo(event.getX(index), event.getY(index));
                paths.put(id, path);
                break;

            case MotionEvent.ACTION_MOVE:
                for (int i=0; i<event.getPointerCount(); i++) {
                    id = event.getPointerId(i);
                    path = paths.get(id);
                    if (path != null) path.lineTo(event.getX(i), event.getY(i));
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                path = paths.get(id);
                if (path != null) {
                    drawCanvas.drawPath(path, drawPaint);
                    paths.remove(id);
                }
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}
