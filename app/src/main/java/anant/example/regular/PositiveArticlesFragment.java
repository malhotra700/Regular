package anant.example.regular;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PositiveArticlesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PositiveArticlesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseDatabase database;
    DatabaseReference ref;
    Button readAloudBtn;
    boolean flag;
    TextToSpeech tts;
    TextView headingTV,authorTV,sourceTV,genreTV,bodyTV;

    public PositiveArticlesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PositiveArticlesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PositiveArticlesFragment newInstance(String param1, String param2) {
        PositiveArticlesFragment fragment = new PositiveArticlesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_positive_articles, container, false);

        headingTV=view.findViewById(R.id.heading_text_view);
        authorTV=view.findViewById(R.id.author_text_view);
        genreTV=view.findViewById(R.id.genre_text_view);
        sourceTV=view.findViewById(R.id.source_text_view);
        bodyTV=view.findViewById(R.id.body_text_view);
        readAloudBtn=view.findViewById(R.id.read_aloud_btn);

        flag=false;

        database=FirebaseDatabase.getInstance();
        ref=database.getReference("Read");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                if(article!=null) {
                    Log.i("Checkerrr", article.getHeading());
                    headingTV.setText(article.getHeading());
                    authorTV.setText(article.getAuthor());
                    sourceTV.setText(article.getSource());
                    genreTV.setText(article.getGenre());
                    bodyTV.setText(article.getBody());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readAloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag){
                    flag=true;
                    readAloudBtn.setText("Stop");
                    tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            Log.e("TTS", String.valueOf(status));
                            if (status == TextToSpeech.SUCCESS) {
                                int result = tts.setLanguage(Locale.US);
                                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e("TTS", "This Language is not supported");
                                }
                                speak((String) bodyTV.getText());

                            } else {
                                Log.e("TTS", "Initilization Failed!");
                            }
                        }
                    });
                }
                else{
                    flag=false;
                    readAloudBtn.setText("Read Aloud");
                    tts.stop();
                }

            }
        });


        return view;
    }
    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}