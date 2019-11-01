package com.example.tubes_2.fragments;

        import android.content.Context;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.Toast;

        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.Volley;
        import com.example.tubes_2.R;
        import com.example.tubes_2.interfaces.UIActivity;
        import com.example.tubes_2.model.RequestObject;
        import com.example.tubes_2.model.Score;
        import com.google.gson.Gson;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

public class MenuFragment extends Fragment implements View.OnClickListener {
    Button startGame,highScore;
    final int START_GAME = 1;
    final int HIGH_SCORE = 2;
    UIActivity activity;

    private static final String BASE_URL = "http://p3b.labftis.net/api.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        this.startGame = view.findViewById(R.id.new_game);
        this.highScore = view.findViewById(R.id.high_score);
        this.startGame.setOnClickListener(this);
        this.highScore.setOnClickListener(this);

        return view;
    }

    public void setActivity(UIActivity activity){this.activity = activity;}

    public static MenuFragment newInstance(UIActivity activity) {
        MenuFragment fragment = new MenuFragment();
        fragment.setActivity(activity);

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==this.startGame.getId()){
            this.activity.changePage(START_GAME);
        } else if (view.getId() == this.highScore.getId()){
            this.showHighScoreFragment();
        }
    }

    public void updateScore(final int score) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        final Gson gson = new Gson();

        String url = BASE_URL + "?api_key=2017730017";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("data");
                            List<Score> scoreList = new ArrayList<>();

                            if (arr.length() > 0) {
                                for (int i = 0; i < 20; i++) {
                                    JSONObject jsonObject = arr.getJSONObject(i);
                                    String str = jsonObject.toString();

                                    Score scorex = gson.fromJson(str, Score.class);

                                    scoreList.add(scorex);
                                }

                                Collections.sort(scoreList);
                            }

                            for (int i = 0; i < 20; i++) {
                                if (scoreList.get(i).getScore() < score) {
                                    sendUpdateScoreRequest(i + 1, score, scoreList);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // DO NOTHING
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }

    public void sendUpdateScoreRequest(final int idx, final int score, List<Score> prevScores) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        Gson gson = new Gson();

        RequestObject req = new RequestObject("2017730017", idx, score);

        String jsonString = gson.toJson(req);

        JSONObject obj = new JSONObject(jsonString);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Context context = getContext();
                        String text = "Successfully update high score at index " + idx + " with score " + score;
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // DO NOTHING
                    }
                }
        );

        queue.add(request);

        for (int i = idx; i < 20; i++) {
            RequestObject requestObject = new RequestObject("2017730017", prevScores.get(i).getOrder() + 1, prevScores.get(i).getScore());

            String jsonStrong = gson.toJson(requestObject);

            JSONObject jsonObject = new JSONObject(jsonStrong);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // DO NOTHING
                        }
                    }
            );

            queue.add(jsonObjectRequest);
        }
    }

    public void showHighScoreFragment() {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        final Gson gson = new Gson();

        String url = BASE_URL + "?api_key=2017730017";

        final List<Score> scoreList = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("data");

                            if (arr.length() > 0) {
                                for (int i = 0; i < 20; i++) {
                                    JSONObject jsonObject = arr.getJSONObject(i);
                                    String str = jsonObject.toString();

                                    Score scorex = gson.fromJson(str, Score.class);

                                    scoreList.add(scorex);
                                }
                            }

                            Collections.sort(scoreList);

                            FragmentManager fm = getFragmentManager();
                            HighScoreFragment fragment = new HighScoreFragment(scoreList);

                            fragment.show(fm, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // DO NOTHING
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }
}
