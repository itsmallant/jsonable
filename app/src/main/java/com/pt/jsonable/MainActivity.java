package com.pt.jsonable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pt.jsonable.R;
import com.pt.jsonable.bean.Factory;
import com.pt.jsonable.bean.Goods;
import com.pt.jsonable.bean.Pit;
import com.pt.jsonable.bean.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void test(View view) {
        List<Goods> buyedGoods = Arrays.asList(
                new Goods("2000", "手机", new Factory("China", "HW", 21000.0F)),
                new Goods("22000", "电脑", new Factory("China", "MAC", 12000.0F))
        );

        Goods[] hateGoods =new Goods[]{
                new Goods("10", "衣服", new Factory("China", "UYK", 1000.0F)),
                new Goods("110", "衣服2", new Factory("China", "UYK2", 100.0F))
        };
        String[] followwers = new String[]{"zhangsan","李四"};


        Pit[] lovePits = new Pit[]{new Pit("Cat",1),new Pit("Dog",2)};

        ArrayList<Pit> hatePits = new ArrayList<>();
        hatePits.add( new Pit("Snack",11));
        hatePits.add( new Pit("Pig",12));
        hatePits.add( new Pit("Spider",13));

        ArrayList<Pit> maybePits = new ArrayList<>();
        User user = new User("Jim",
                12,
                buyedGoods,
                hateGoods,
                followwers,
                lovePits,
                hatePits,
                new Factory("USA","Google",5200f),
                maybePits
                );
        JSONObject convert = JsonKnife.convert(user);
        Log.e(TAG,"convert user = \r\n"+convert);
    }

    private static final String TAG = "MainActivity";
}
