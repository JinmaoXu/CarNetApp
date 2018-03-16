package com.tjpu.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
/**
 * 
 * @author jinmaoxu
 *
 */
public class AutoSrollTextView extends TextView {  
    /** ���ֳ��� */  
    private float textLength = 0f;  
    /** ���������� */  
    private float viewWidth = 0f;  
    /** �ı�x�� ������ */  
    private float tx = 0f;  
    /** �ı�Y������� */  
    private float ty = 0f;  
    /** �ı���ǰ���� */  
    private float temp_tx1 = 0.0f;  
    /** �ı���ǰ�任�ĳ��� */  
    private float temp_tx2 = 0x0f;  
    /** �ı��������� */  
    private boolean isStarting = false;  
    /** ���ʶ��� */  
    private Paint paint = null;  
    /** ��ʾ������ */  
    private String text = "";  
  
    public AutoSrollTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    /**  
     * ��ʼ���Զ�������,ÿ�θı���������ʱ������Ҫ���³�ʼ��һ��  
     *   
     * @param windowManager  
     *            ��ȡ��Ļ  
     * @param text  
     *            ��ʾ������  
     */  
    public void initScrollTextView(WindowManager windowManager, String text) {  
        // �õ�����,��ȡ�����textPaint  
        paint = this.getPaint();  
        // �õ�����  
        this.text = text;  
  
        textLength = paint.measureText(text);// ��õ�ǰ�ı��ַ�������  
        viewWidth = this.getWidth();// ��ȡ���return mRight - mLeft;  
        if (viewWidth == 0) {  
            if (windowManager != null) {  
                // ��ȡ��ǰ��Ļ������  
                Display display = windowManager.getDefaultDisplay();  
                viewWidth = display.getWidth();// ��ȡ��Ļ���  
  
            }  
        }  
        tx = textLength;  
        temp_tx1 = viewWidth + textLength;  
        temp_tx2 = viewWidth + textLength * 2;// �Լ����壬���ֱ仯����  
        // ���ֵĴ�С+�ඥ���ľ���  
        ty = this.getTextSize() + this.getPaddingTop();  
    }  
  
    /**  
     * ��ʼ����  
     */  
    public void starScroll() {  
        // ��ʼ����  
        isStarting = true;  
        this.invalidate();// ˢ����Ļ  
    }  
  
    /**  
     * ֹͣ����,ֹͣ����  
     */  
    public void stopScroll() {  
        // ֹͣ����  
        isStarting = false;  
        this.invalidate();// ˢ����Ļ  
    }  
  
    /** ��дonDraw���� */  
    @Override  
    protected void onDraw(Canvas canvas) {  
        if (isStarting) {  
            // A-Alpha͸����/R-Read��ɫ/g-Green��ɫ/b-Blue��ɫ  
            paint.setARGB(255, 200, 200, 200);  
            canvas.drawText(text, temp_tx1 - tx, ty, paint);  
            tx += 0.4;  
            // �����ֹ�������Ļ�������  
            if (tx >= temp_tx2) {  
                // ���������õ����ұ߿�ʼ  
                tx = temp_tx1 - viewWidth;  
            }  
            this.invalidate();// ˢ����Ļ  
        }  
        super.onDraw(canvas);  
    }  
}  