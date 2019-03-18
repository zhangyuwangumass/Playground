package com.octopus.pro.a21points;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by pro on 16/9/16.
 */
public class PlaygroundActivity extends AppCompatActivity {

    /**基本组件
     * PlaygroundActivity全局引用
     */

    //Handler，处理多线程任务
    Handler handler = new Handler();
    //本地数据
    SharedPreferences savedUserData;


    //叫牌弹窗
    PopupWindow callWindow;
    //筹码弹窗
    PopupWindow chipsWindow;
    //发牌按钮
    Button dealButton;
    //叫牌按钮
    Button callButton;
    //发牌空位
    ImageView cardNiche;

    //筹码显示栏
    TextView chipsAmount;
    //下注数量显示栏
    TextView betSum;
    //下注倍数显示栏
    TextView betMulti;
    //游戏结果显示栏
    TextView resultText;

    //玩家手牌
    ImageView handCard0, handCard1, handCard2, handCard3, handCard4;
    //电脑手牌
    ImageView computerCard0, computerCard1, computerCard2, computerCard3, computerCard4;

    //发牌动画
    AnimationDrawable cardDealing;
    //发牌动画区域
    ImageView cardDealingAnim;

    /**
     * 游戏数据
     */

    //筹码数
    int chips = 0;
    //已发牌数：cardNo可取0，1，2，3，4
    int cardNo = 0;
    //玩家点数
    int playerPoint = 0;
    //电脑点数
    int computerPoint = 0;

    //每次发牌时玩家、电脑所得牌号（牌号＝0-51，对应于drawable图库中的poker_small_X资源文件
    int player, computer;
    //储存每次发牌后结果；－1表示该位置无牌
    int playerCard[] = {-1, -1, -1, -1, -1}; int computerCard[] = {-1, -1, -1, -1, -1};
    //使用String存储一副牌的被抽取情况，1代表该位置牌尚未抽取，0代表该位置牌已抽取；采用String是为了便于存入SharedPreference文件
    String deck;
    //控制按钮是否可点击
    boolean clickable = true;
    //判断是否正在运行发牌动画；发牌动画中不可进行除退出外的操作
    boolean isShifting = false;

    //下注数
    int betsum = 0;
    //倍数
    int multiplier = 1;
    //下注池
    int betPool = 0;
    //坐标点
    float x1, y1, x2, y2 = 0;


    /**
     * 初始化事件
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playground);
        loadUserInfo();

        dealButton = (Button)findViewById(R.id.playground_button_deal);
        callButton = (Button)findViewById(R.id.playground_button_call);
        dealButton.setOnClickListener(buttonListener);
        callButton.setOnClickListener(buttonListener);

        drawPlayerCardArea();

        clickable = savedUserData.getBoolean("clickable", true);

        deck = savedUserData.getString("deck", getResources().getString(R.string.start_deck));

        chipsAmount = (TextView)findViewById(R.id.chips_amount);
        chips = savedUserData.getInt("chips", 1000);
        chipsAmount.setText("" + chips);
        resultText = (TextView) findViewById(R.id.text_result);
        resultText.setText(savedUserData.getString("resultText", "Player Win"));
        if(savedUserData.getBoolean("resultVisibility", false)) {
            resultText.setVisibility(View.VISIBLE);
            drawComputerCardArea();
        }
        else resultText.setVisibility(View.GONE);

        playerPoint = savedUserData.getInt("player", 0);
        computerPoint = savedUserData.getInt("computer", 0);

        cardDealing = (AnimationDrawable) getResources().getDrawable(R.drawable.card_dealing);
        cardDealingAnim = (ImageView)findViewById(R.id.card_dealing_anim);

        startService(new Intent(PlaygroundActivity.this, MyService.class));
    }


    /**
     * 事件暂停时，将需要存储的游戏数据存入SharedPreference中
     */
    @Override
    protected void onPause() {
        super.onPause();
        (savedUserData.edit()).putString("deck", deck).putInt("player", playerPoint).
                putInt("computer", computerPoint).putInt("chips", chips).putInt("handcard0", playerCard[0]).
                putInt("handcard1", playerCard[1]).putInt("handcard2", playerCard[2]).
                putInt("handcard3", playerCard[3]).putInt("handcard4", playerCard[4]).
                putInt("computercard0", computerCard[0]).putInt("computercard1", computerCard[1]).
                putInt("computercard2", computerCard[2]).putInt("computercard3", computerCard[3]).
                putInt("computercard4", computerCard[4]).putBoolean("clickable", clickable).
                putBoolean("resultVisibility", (resultText.getVisibility() == View.VISIBLE)).putInt("cardNo", cardNo).putString("resultText", resultText.getText().toString()).apply();

        stopService(new Intent(PlaygroundActivity.this, MyService.class));
    }


    /**
     * 统一的按键监听器
     */
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.playground_button_deal && clickable && !isShifting){
                //need implementation
                dealCard();
            }
            if(view.getId() == R.id.playground_button_call && !isShifting){
                //need implementation
                System.out.println("call is clicked");
                getCallWindow();
                callWindow.showAsDropDown(findViewById(R.id.playground_button_call), 70, -650);
            }
        }
    };


    /**
     * 统一的菜单项监听器
     */
    private View.OnClickListener callItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.BET && clickable){
                //need implementation
                getChipsWindow();
                chipsWindow.showAsDropDown(findViewById(R.id.playground_button_deal), -250, -600);
                dealButton.setText("BET");
            }
            else if(view.getId() == R.id.DOUBLE && clickable){
                //need implementation
                multiplier *= 2;
            }
            else if(view.getId() == R.id.THROW && clickable){
                //need implementation
                clickable = false;
                resultText.setText("You Lose!");
                resultText.setVisibility(View.VISIBLE);
            }
            else if(view.getId() == R.id.GO && clickable){
                //need implementation
                result();
            }
            else if(view.getId() == R.id.RESTART){
                // Restart the deck
                restart();
            }
            if (callWindow != null && callWindow.isShowing()) {
                callWindow.dismiss();
                callWindow = null;
            }
        }
    };


    /**
     * 筹码盘监听器
     */
    private View.OnTouchListener chipListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                x1 = motionEvent.getX();
                y1 = motionEvent.getY();
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                x2 = motionEvent.getX();
                y2 = motionEvent.getY();
                if((x2 - x1) > 20 || (y2 - y1) > 20 || (x1 - x2) > 20 || (y1 - y2) > 20){
                    int id = view.getId();
                    int bufferSum = betsum;
                    switch (id) {
                        case R.id.chips_25:
                            bufferSum -= 25;
                            break;
                        case R.id.chips_50:
                            bufferSum -= 50;
                            break;
                        case R.id.chips_100:
                            bufferSum -= 100;
                            break;
                        case R.id.chips_500:
                            bufferSum -= 500;
                            break;
                        case R.id.chips_1000:
                            bufferSum -= 1000;
                            break;
                        case R.id.chips_2500:
                            bufferSum -= 2500;
                            break;
                        case R.id.chips_5000:
                            bufferSum -= 5000;
                            break;
                        case R.id.chips_10000:
                            bufferSum -= 10000;
                            break;
                        case R.id.chips_50000:
                            bufferSum -= 50000;
                            break;
                        case R.id.chips_100000:
                            bufferSum -= 100000;
                            break;
                        case R.id.chips_500000:
                            bufferSum -= 500000;
                            break;
                        case R.id.chips_1000000:
                            bufferSum -= 1000000;
                            break;
                    }
                    if(isBetValid(bufferSum)) {
                        betsum = bufferSum;
                        betSum.setText("" + betsum);
                    }
                }
                else{
                    int id = view.getId();
                    int bufferSum = betsum;
                    switch (id) {
                        case R.id.chips_25:
                            bufferSum += 25;
                            break;
                        case R.id.chips_50:
                            bufferSum += 50;
                            break;
                        case R.id.chips_100:
                            bufferSum += 100;
                            break;
                        case R.id.chips_500:
                            bufferSum += 500;
                            break;
                        case R.id.chips_1000:
                            bufferSum += 1000;
                            break;
                        case R.id.chips_2500:
                            bufferSum += 2500;
                            break;
                        case R.id.chips_5000:
                            bufferSum += 5000;
                            break;
                        case R.id.chips_10000:
                            bufferSum += 10000;
                            break;
                        case R.id.chips_50000:
                            bufferSum += 50000;
                            break;
                        case R.id.chips_100000:
                            bufferSum += 100000;
                            break;
                        case R.id.chips_500000:
                            bufferSum += 500000;
                            break;
                        case R.id.chips_1000000:
                            bufferSum += 1000000;
                            break;
                    }
                    if(isBetValid(bufferSum)) {
                        betsum = bufferSum;
                        betSum.setText("" + betsum);
                    }
                }
            }

            return false;
        }
    };


    /**
     * 初始化筹码弹窗
     */
    protected void initChipsWindow() {
        System.out.println("chips init");
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        final View popupWindow_view = getLayoutInflater().inflate(R.layout.popmenu_chips, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        chipsWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        chipsWindow.setAnimationStyle(R.anim.pop_enter);
        // 点击其他地方消失
        chipsWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                System.out.println("Touch Success");

                float x = event.getRawX();
                float y = event.getRawY();
                System.out.println((x > dealButton.getLeft() && x < dealButton.getRight() && y > dealButton.getTop() && y < dealButton.getBottom()));
                System.out.println(x + " " + y);
                System.out.println(popupWindow_view.getLeft() + " " + popupWindow_view.getRight() + " " + popupWindow_view.getTop() + " " + popupWindow_view.getBottom());
                if(x > dealButton.getLeft() && x < dealButton.getRight() && y > dealButton.getTop() && y < dealButton.getBottom()) {
                    System.out.println("bet is clicked");
                    chips -= betsum;
                    betPool += betsum;
                    System.out.println(chips);
                }
                else if(x > 170 && x < popupWindow_view.getRight() && y > 90 && y < popupWindow_view.getBottom()){
                    System.out.println("chipsWindow is clicked");
                    return false;
                }
                System.out.println("worked until here");
                chipsAmount.setText("" + chips);
                dealButton.setText("DEAL");
                betsum = 0;
                chipsWindow.dismiss();
                chipsWindow = null;

                return true;
            }
        });

        ImageButton[] chips = new ImageButton[12];
        chips[0] = (ImageButton) popupWindow_view.findViewById(R.id.chips_25);
        chips[1] = (ImageButton) popupWindow_view.findViewById(R.id.chips_50);
        chips[2] = (ImageButton) popupWindow_view.findViewById(R.id.chips_100);
        chips[3] = (ImageButton) popupWindow_view.findViewById(R.id.chips_500);
        chips[4] = (ImageButton) popupWindow_view.findViewById(R.id.chips_1000);
        chips[5] = (ImageButton) popupWindow_view.findViewById(R.id.chips_2500);
        chips[6] = (ImageButton) popupWindow_view.findViewById(R.id.chips_5000);
        chips[7] = (ImageButton) popupWindow_view.findViewById(R.id.chips_10000);
        chips[8] = (ImageButton) popupWindow_view.findViewById(R.id.chips_50000);
        chips[9] = (ImageButton) popupWindow_view.findViewById(R.id.chips_100000);
        chips[10] = (ImageButton) popupWindow_view.findViewById(R.id.chips_500000);
        chips[11] = (ImageButton) popupWindow_view.findViewById(R.id.chips_1000000);

        for(int i = 0; i < 12; i++) {
            chips[i].setOnTouchListener(chipListener);
        }

        betSum = (TextView) popupWindow_view.findViewById(R.id.bet_sum);
        betMulti = (TextView) popupWindow_view.findViewById(R.id.bet_multiplier);
    }


    /**
     * 初始化叫牌弹窗
     */
    protected void initCallWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        View popupWindow_view = getLayoutInflater().inflate(R.layout.popmenu_call, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        callWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        callWindow.setAnimationStyle(R.anim.pop_enter);
        // 点击其他地方消失

        Button BET = (Button)popupWindow_view.findViewById(R.id.BET);
        Button DOUBLE = (Button)popupWindow_view.findViewById(R.id.DOUBLE);
        Button THROW = (Button)popupWindow_view.findViewById(R.id.THROW);
        Button GO = (Button)popupWindow_view.findViewById(R.id.GO);
        Button RESTART = (Button)popupWindow_view.findViewById(R.id.RESTART);

        BET.setOnClickListener(callItemListener);
        DOUBLE.setOnClickListener(callItemListener);
        THROW.setOnClickListener(callItemListener);
        GO.setOnClickListener(callItemListener);
        RESTART.setOnClickListener(callItemListener);
    }

    /**
     * 获取PopupWindow实例
     */
    private void getChipsWindow() {
        //need implementation
        initChipsWindow();
    }


    private void getCallWindow() {
        //need implementation
        initCallWindow();
    }


    /**
     * 数据清零
     */
    private void restart(){
        playerPoint = 0;
        computerPoint = 0;
        betPool = 0;
        cardNo = 0;
        deck = getResources().getString(R.string.start_deck);
        handCard0.setImageDrawable(null);
        handCard1.setImageDrawable(null);
        handCard2.setImageDrawable(null);
        handCard3.setImageDrawable(null);
        handCard4.setImageDrawable(null);
        computerCard0.setImageDrawable(null);
        computerCard1.setImageDrawable(null);
        computerCard2.setImageDrawable(null);
        computerCard3.setImageDrawable(null);
        computerCard4.setImageDrawable(null);
        for(int i = 0; i < 5; i++)
            playerCard[i] = -1;
        for(int i = 0; i < 5; i++)
            computerCard[i] = -1;
        resultText.setVisibility(View.GONE);
        clickable = true;
    }


    /**
     * 发牌
     */
    private void dealCard(){
        cardDealingAnim.setImageDrawable(cardDealing);
        cardDealingAnim.setVisibility(View.VISIBLE);
        cardDealing.start();
        isShifting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                boolean found = false;
                try{
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    System.out.println("dealing thread interrupted");
                }
                System.out.println("sleep over");
                while(!found){
                    player = rand.nextInt(52);
                    playerCard[cardNo] = player;
                    playerPoint = pointCount(playerPoint, player);
                    computer = rand.nextInt(52);
                    if(player != computer && deck.charAt(player) == '1' && deck.charAt(computer) == '1'){
                        switch (cardNo){
                            case 0:
                                cardNiche = handCard0;
                                break;
                            case 1:
                                cardNiche = handCard1;
                                break;
                            case 2:
                                cardNiche = handCard2;
                                break;
                            case 3:
                                cardNiche = handCard3;
                                break;
                            case 4:
                                cardNiche = handCard4;
                                break;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                final int imgId = getResources().getIdentifier("poker_small_" + (player + 1), "drawable", "com.octopus.pro.a21points");
                                cardDealingAnim.setImageResource(imgId);
                                cardDealing.stop();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            Thread.sleep(2000);
                                        }catch (InterruptedException e){
                                            System.out.println("dealing thread interrupted");
                                        }
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                cardDealingAnim.setVisibility(View.INVISIBLE);
                                                cardNiche.setImageResource(imgId);
                                                isShifting = false;
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                        StringBuilder builder = new StringBuilder(deck);
                        builder.setCharAt(player, '0');
                        builder.setCharAt(computer, '0');
                        deck = builder.toString();
                        System.out.println(deck);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                switch (computerDecide()){
                                    // 0 means "Call for Next Card", 1 means "Double", -1 means "THROW", 2 means "GO"
                                    case -1: resultText.setVisibility(View.VISIBLE); break;
                                    case 0:
                                        computerPoint = pointCount(computerPoint, computer);
                                        computerCard[cardNo] = computer;
                                        break;
                                    case 1: break;
                                    case 2:
                                        computerPoint = pointCount(computerPoint, computer);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                result();
                                            }
                                        });
                                        break;
                                }
                            }
                        }).start();
                        cardNo++;
                        // Better save on OnStop
                        found = true;
                    }
                }
            }
        }).start();
    }


    /**
     * 显示游戏结果
     */
    private void result(){
        System.out.println("Points are " + playerPoint + " " + computerPoint);
        clickable = false;
        String info;
        drawComputerCardArea();
        if(playerPoint > 21 && computerPoint > 21)
            info = "Both Lose!";
        else if(playerPoint > 21)
            info = "Player Lose!";
        else if(computerPoint > 21) {
            info = "Computer Lose!";
            chips += betPool;
            chipsAmount.setText("" + chips);
        }
        else if(playerPoint > computerPoint) {
            info = "Player Win!";
            chips += awardCount(true);
            chipsAmount.setText("" + chips);
        }
        else if(playerPoint < computerPoint) {
            info = "Computer Win!";
            chips += awardCount(false);
            chipsAmount.setText("" + chips);
        }
        else info = "Draw!";

        resultText.setText(info);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println(e);
                }
                resultText.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 计算奖励分；可以重写或调用新的奖励分计算函数
     * @param hasWon
     * @return
     */
    private int awardCount(boolean hasWon){

        //need application

        int award = 0;
        if(hasWon)
        award = betPool * multiplier + betPool;
        else award = -(betPool * multiplier);
        return award;
    }


    /**
     * 计算机AI
     * @return
     */
    private int computerDecide() {
        int decision = 0;

        // AI Core
        // 0 means follow, 1 means double, 2 means go, -1 means throw

        return decision;
    }


    /**
     * 向Handler发送计算机AI决策结果
     * @param decision
     */
    private void postComputerDecision(int decision){
        switch (decision){

        }
    }


    /**
     * 检测是否可以继续下注
     * @param bufferSum
     * @return
     */
    private boolean isBetValid(int bufferSum){
        return (chips - bufferSum >= 0 && bufferSum >= 0);
    }


    /**
     * 计算点数
     * @param base
     * @param index
     * @return
     */
    private int pointCount(int base, int index) {
        int point = index%13;
        if(point < 10)
            point = base + point + 1;
        else point = base + 10;
        return point;
    }


    /**
     * 加载用户信息
     */
    public void loadUserInfo(){

        // Load User basic info
        String email = getIntent().getExtras().getString("email");
        String username = getIntent().getExtras().getString("username");
        String password = getIntent().getExtras().getString("password");
        savedUserData = getSharedPreferences(username, MODE_PRIVATE);
        SharedPreferences.Editor savedUserDataEditor = savedUserData.edit();
        savedUserDataEditor.putBoolean("exists", true).putString("email", email).putString("username", username)
                .putString("password", password).putInt("chips", savedUserData.getInt("chips", 1000)).apply();

        int index;
        if((index = savedUserData.getInt("handcard0", -1)) != -1)
            playerCard[0] = index;
        if((index = savedUserData.getInt("handcard1", -1)) != -1)
            playerCard[1] = index;
        if((index = savedUserData.getInt("handcard2", -1)) != -1)
            playerCard[2] = index;
        if((index = savedUserData.getInt("handcard3", -1)) != -1)
            playerCard[3] = index;
        if((index = savedUserData.getInt("handcard4", -1)) != -1)
            playerCard[4] = index;

        if ((index = savedUserData.getInt("computercard0", -1)) != -1)
            computerCard[0] = index;

        if ((index = savedUserData.getInt("computercard1", -1)) != -1)
            computerCard[1] = index;

        if ((index = savedUserData.getInt("computercard2", -1)) != -1)
            computerCard[2] = index;

        if ((index = savedUserData.getInt("computercard3", -1)) != -1)
            computerCard[3] = index;

        if ((index = savedUserData.getInt("computercard4", -1)) != -1)
            computerCard[4] = index;
        // Load User game info

        cardNo = savedUserData.getInt("cardNo", 0);
    }


    /**
     * 画玩家牌区
     */
    private void drawPlayerCardArea(){
        int imgId;
        handCard0 = (ImageView) findViewById(R.id.handcard_0);
        imgId = getResources().getIdentifier("poker_small_" + (playerCard[0] + 1), "drawable", "com.octopus.pro.a21points");
        handCard0.setImageResource(imgId);

        handCard1 = (ImageView) findViewById(R.id.handcard_1);
        imgId = getResources().getIdentifier("poker_small_" + (playerCard[1] + 1), "drawable", "com.octopus.pro.a21points");
        handCard1.setImageResource(imgId);

        handCard2 = (ImageView) findViewById(R.id.handcard_2);
        imgId = getResources().getIdentifier("poker_small_" + (playerCard[2] + 1), "drawable", "com.octopus.pro.a21points");
        handCard2.setImageResource(imgId);

        handCard3 = (ImageView) findViewById(R.id.handcard_3);
        imgId = getResources().getIdentifier("poker_small_" + (playerCard[3] + 1), "drawable", "com.octopus.pro.a21points");
        handCard3.setImageResource(imgId);

        handCard4 = (ImageView) findViewById(R.id.handcard_4);
        imgId = getResources().getIdentifier("poker_small_" + (playerCard[4] + 1), "drawable", "com.octopus.pro.a21points");
        handCard4.setImageResource(imgId);
    }


    /**
     * 画电脑牌区
     */
    private void drawComputerCardArea(){
        int index; int imgId;

        computerCard0 = (ImageView) findViewById(R.id.computercard_0);
        computerCard1 = (ImageView) findViewById(R.id.computercard_1);
        computerCard2 = (ImageView) findViewById(R.id.computercard_2);
        computerCard3 = (ImageView) findViewById(R.id.computercard_3);
        computerCard4 = (ImageView) findViewById(R.id.computercard_4);


        if ((index = computerCard[0]) != -1) {
            imgId = getResources().getIdentifier("poker_small_" + (index + 1), "drawable", "com.octopus.pro.a21points");
            computerCard0.setImageResource(imgId);
        }

        if ((index = computerCard[1]) != -1) {
            imgId = getResources().getIdentifier("poker_small_" + (index + 1), "drawable", "com.octopus.pro.a21points");
            computerCard1.setImageResource(imgId);
        }

        if ((index = computerCard[2]) != -1) {
            imgId = getResources().getIdentifier("poker_small_" + (index + 1), "drawable", "com.octopus.pro.a21points");
            computerCard2.setImageResource(imgId);
        }

        if ((index = computerCard[3]) != -1) {
            imgId = getResources().getIdentifier("poker_small_" + (index + 1), "drawable", "com.octopus.pro.a21points");
            computerCard3.setImageResource(imgId);
        }

        if ((index = computerCard[4]) != -1) {
            imgId = getResources().getIdentifier("poker_small_" + (index + 1), "drawable", "com.octopus.pro.a21points");
            computerCard4.setImageResource(imgId);
        }
    }

}
