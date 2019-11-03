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

        import com.android.volley.AuthFailureError;
        import com.android.volley.NetworkResponse;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.HttpHeaderParser;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.Volley;
        import com.example.tubes_2.R;
        import com.example.tubes_2.interfaces.UIActivity;
        import com.example.tubes_2.model.Score;
        import com.example.tubes_2.util.MultipartRequest;
        import com.google.gson.Gson;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

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
        if (view.getId() == this.startGame.getId()){
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
                            }

                            for (int i = 0; i < 20; i++) {
                                if (scoreList.get(i).getScore() < score) {
                                    sendUpdateScoreRequest(scoreList.get(i).getOrder(), score, scoreList);
                                    break;
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

    public void sendUpdateScoreRequest(final int idx, final int score, final List<Score> prevScores) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        MultipartRequest request = new MultipartRequest(
                Request.Method.POST,
                BASE_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
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
                        System.out.println(error.getMessage());
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("api_key", "2017730017");
                params.put("order", Integer.toString(idx));
                params.put("value", Integer.toString(score));

                return params;
            }
        };

        queue.add(request);

        for (int i = idx; i < 20; i++) {
            final int orig = i;
            final int order = i + 1;

            MultipartRequest jsonObjectRequest = new MultipartRequest(
                    Request.Method.POST,
                    BASE_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // DO NOTHING
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> paramex = new HashMap<>();
                    paramex.put("api_key", "2017730017");
                    paramex.put("order", Integer.toString(order));
                    paramex.put("value", Integer.toString(prevScores.get(orig - 1).getScore()));

                    return paramex;
                }
            };

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
