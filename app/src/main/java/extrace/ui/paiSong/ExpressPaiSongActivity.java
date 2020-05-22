package extrace.ui.paiSong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import extrace.misc.model.ExpressSheet;
import extrace.ui.main.R;
import extrace.ui.packages.ExpressInPacListAdapter;

public class ExpressPaiSongActivity extends AppCompatActivity {

    private ListView daiPaiSonglistView;
    private Button addExpBtn;
    private Button startPaiSong;
    private ExpressInPacListAdapter expressInPacListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_pai_song);
        initView();
        initData();
    }

    private void initView() {
        daiPaiSonglistView = (ListView)findViewById(R.id.daiPaiSong_list);
        addExpBtn = (Button)findViewById(R.id.paisong_addexp_btn);
        startPaiSong = (Button)findViewById(R.id.paisong_start_btn);
    }

    private void initData() {
        expressInPacListAdapter = new ExpressInPacListAdapter(new ArrayList<ExpressSheet>(),this);
        daiPaiSonglistView.setAdapter(expressInPacListAdapter);
        addExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpress();
            }
        });
        startPaiSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartPaiSong();
            }
        });
    }

    //开始派送
    private void StartPaiSong() {
    }

    //添加快件
    private void addExpress() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
