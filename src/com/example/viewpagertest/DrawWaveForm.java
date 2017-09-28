package com.example.viewpagertest;

import java.io.File;
import java.io.FileInputStream;

import com.example.ViewClass.MarkerView;
import com.example.ViewClass.WaveformView;
import com.ringdroid.soundfile.CheapSoundFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class DrawWaveForm extends View 
	implements MarkerView.MarkerListener,
	WaveformView.WaveformListener
{

	
	 private long mLoadingLastUpdateTime;
	    private boolean mLoadingKeepGoing;
	    private ProgressDialog mProgressDialog;
	    private CheapSoundFile mSoundFile;
	    private File mFile;
	    private String mFilename;
	    private String mArtist;
	    private String mTitle;
	    private WaveformView mWaveformView;
	    private MarkerView mStartMarker;
	    private MarkerView mEndMarker;
	    private TextView mStartText;
	    private TextView mEndText;
	    private TextView mTitleText;
	    private TextView mInfo;
	    private ImageButton mPlayButton;
	    private ImageButton mRewindButton;
	    private ImageButton mFfwdButton;
	    private ImageButton mZoomInButton;
	    private ImageButton mZoomOutButton;
	    private ImageButton mSaveButton;
	    private boolean mKeyDown;
	    private String mCaption = "";
	    private int mWidth;
	    private int mMaxPos;
	    private int mStartPos;
	    private int mEndPos;
	    private boolean mStartVisible;
	    private boolean mEndVisible;
	    private int mLastDisplayedStartPos;
	    private int mLastDisplayedEndPos;
	    private int mOffset;
	    private int mOffsetGoal;
	    private int mFlingVelocity;
	    private int mPlayStartMsec;
	    private int mPlayStartOffset;
	    private int mPlayEndMsec;
	    private Handler mHandler;
	    private boolean mIsPlaying;
	    private MediaPlayer mPlayer;
	    private boolean mCanSeekAccurately;
	    private boolean mTouchDragging;
	    private float mTouchStart;
	    private int mTouchInitialOffset;
	    private int mTouchInitialStartPos;
	    private int mTouchInitialEndPos;
	    private long mWaveformTouchStartMsec;
	    private float mDensity;
	    private int mMarkerLeftInset;
	    private int mMarkerRightInset;
	    private int mMarkerTopOffset;
	    private int mMarkerBottomOffset;

	    /**
	     * This is a special intent action that means "edit a sound file".
	     */
	    public static final String EDIT =
	        "com.ringdroid.action.EDIT";

	    /**
	     * Preference names
	     */
	    public static final String PREF_SUCCESS_COUNT = "success_count";

	    public static final String PREF_STATS_SERVER_CHECK =
	        "stats_server_check";
	    public static final String PREF_STATS_SERVER_ALLOWED =
	        "stats_server_allowed";

	    public static final String PREF_ERROR_COUNT = "error_count";

	    public static final String PREF_ERR_SERVER_CHECK =
	        "err_server_check";
	    public static final String PREF_ERR_SERVER_ALLOWED =
	        "err_server_allowed";

	    public static final String PREF_UNIQUE_ID = "unique_id";

	    /**
	     * Possible codes for PREF_*_SERVER_ALLOWED
	     */
	    public static final int SERVER_ALLOWED_UNKNOWN = 0;
	    public static final int SERVER_ALLOWED_NO = 1;
	    public static final int SERVER_ALLOWED_YES = 2;
	
	private Context context;
	private View view;
	public DrawWaveForm(Context context, AttributeSet attrs,View view) {
		super(context, attrs);
		this.context=context;
		this.view=view;
		view.setFocusable(true);  
		view.setFocusableInTouchMode(true);  
		view.requestFocus();
		// TODO Auto-generated constructor stub
		mFilename = "/storage/emulated/0/aaaa.mp3";
		mHandler = new Handler();

        loadGui();

        mHandler.postDelayed(mTimerRunnable, 100);

        if (!mFilename.equals("record")) {
            loadFromFile();
        }
		
	}
	
	private void loadGui() {
        // Inflate our UI from its XML layout description.

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int)(46 * mDensity);
        mMarkerRightInset = (int)(48 * mDensity);
        mMarkerTopOffset = (int)(10 * mDensity);
        mMarkerBottomOffset = (int)(10 * mDensity);

        mStartText = (TextView)view.findViewById(R.id.starttext);
//        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = (TextView)view.findViewById(R.id.endtext);
//        mEndText.addTextChangedListener(mTextWatcher);
        
        mTitleText = (TextView)view.findViewById(R.id.titleText);
        
        mPlayButton = (ImageButton)view.findViewById(R.id.play);
//        mPlayButton.setOnClickListener(mPlayListener);
        mRewindButton = (ImageButton)view.findViewById(R.id.rew);
//        mRewindButton.setOnClickListener(mRewindListener);
        mFfwdButton = (ImageButton)view.findViewById(R.id.ffwd);
//        mFfwdButton.setOnClickListener(mFfwdListener);
        mZoomInButton = (ImageButton)view.findViewById(R.id.zoom_in);
//        mZoomInButton.setOnClickListener(mZoomInListener);
        mZoomOutButton = (ImageButton)view.findViewById(R.id.zoom_out);
//        mZoomOutButton.setOnClickListener(mZoomOutListener);
        mSaveButton = (ImageButton)view.findViewById(R.id.save);
//        mSaveButton.setOnClickListener(mSaveListener);

        TextView markStartButton = (TextView) view.findViewById(R.id.mark_start);
//        markStartButton.setOnClickListener(mMarkStartListener);
        TextView markEndButton = (TextView) view.findViewById(R.id.mark_end);
//        markEndButton.setOnClickListener(mMarkStartListener);

        enableDisableButtons();

        mWaveformView = (WaveformView)view.findViewById(R.id.waveform);
        mWaveformView.setListener(this);

        mInfo = (TextView)view.findViewById(R.id.info);
        mInfo.setText(mCaption);

        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = (MarkerView)view.findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
	mStartVisible = true;

        mEndMarker = (MarkerView)view.findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
	mEndVisible = true;

        updateDisplay();
    }

	private String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf('.'),
                                  filename.length());
    }
	
    private void loadFromFile() {
        mFile = new File(mFilename);

        SongMetadataReader metadataReader = new SongMetadataReader((Activity) context, mFilename);
        mTitle = metadataReader.mTitle;
        mArtist = metadataReader.mArtist;
        
        String titleLabel = mTitle;
        if (mArtist != null && mArtist.length() > 0) {
            titleLabel += " - " + mArtist;
        }
        mTitleText.setText(titleLabel);

        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(
            new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    mLoadingKeepGoing = false;
                }
            });
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener =
            new CheapSoundFile.ProgressListener() {
                public boolean reportProgress(double fractionComplete) {
                    long now = System.currentTimeMillis();
                    if (now - mLoadingLastUpdateTime > 100) {
                        mProgressDialog.setProgress(
                            (int)(mProgressDialog.getMax() *
                                  fractionComplete));
                        mLoadingLastUpdateTime = now;
                    }
                    return mLoadingKeepGoing;
                }
            };

        // Create the MediaPlayer in a background thread
        mCanSeekAccurately = false;
        new Thread() {
            public void run() {
                mCanSeekAccurately = SeekTest.CanSeekAccurately(
                		((Activity) context).getPreferences(Context.MODE_PRIVATE));

                System.out.println("Seek test done, creating media player.");
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepare();
                    mPlayer = player;
                } catch (final java.io.IOException e) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            handleFatalError(
                                "ReadError",
                                getResources().getText(R.string.read_error),
                                e);
                        }
                    };
                    mHandler.post(runnable);
                };
            }
        }.start();

        // Load the sound file in a background thread
        new Thread() { 
            public void run() { 
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(),
                                                       listener);

                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = getResources().getString(
                                R.string.no_extension_error);
                        } else {
                            err = getResources().getString(
                                R.string.bad_extension_error) + " " +
                                components[components.length - 1];
                        }
                        final String finalErr = err;
                        Runnable runnable = new Runnable() {
                            public void run() {
                                handleFatalError(
                                  "UnsupportedExtension",
                                  finalErr,
                                  new Exception());
                            }
                        };
                        mHandler.post(runnable);
                        return;
                    }
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    mInfo.setText(e.toString());

                    Runnable runnable = new Runnable() {
                            public void run() {
                                handleFatalError(
                                  "ReadError",
                                  getResources().getText(R.string.read_error),
                                  e);
                            }
                        };
                    mHandler.post(runnable);
                    return;
                }
                mProgressDialog.dismiss(); 
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                            public void run() {
                                finishOpeningSoundFile();
                            }
                        };
                    mHandler.post(runnable);
                } else {
                    
                }
            } 
        }.start();
    }
	
    
    private void handleFatalError(
            final CharSequence errorInternalName,
            final CharSequence errorString,
            final Exception exception) {
        Log.i("Ringdroid", "handleFatalError");

        SharedPreferences prefs = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        int failureCount = prefs.getInt(PREF_ERROR_COUNT, 0);
        final SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(PREF_ERROR_COUNT, failureCount + 1);
        prefsEditor.commit();

        // Check if we already have a pref for whether or not we can
        // contact the server.
        int serverAllowed = prefs.getInt(PREF_ERR_SERVER_ALLOWED,
                                         SERVER_ALLOWED_UNKNOWN);

        if (serverAllowed == SERVER_ALLOWED_NO) {
            Log.i("Ringdroid", "ERR: SERVER_ALLOWED_NO");

            // Just show a simple "write error" message
            showFinalAlert(exception, errorString);
            return;
        }

        if (serverAllowed == SERVER_ALLOWED_YES) {
            Log.i("Ringdroid", "SERVER_ALLOWED_YES");

            new AlertDialog.Builder(context)
                .setTitle(R.string.alert_title_failure)
                .setMessage(errorString)
                .setPositiveButton(
                    R.string.alert_ok_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            
                            return;
                        }
                    })
                .setCancelable(false)
                .show();
            return;
        }

        // The number of times the user must have had a failure before
        // we'll ask them.  Defaults to 1, and each time they click "Later"
        // we double and add 1.
        final int allowServerCheckIndex =
            prefs.getInt(PREF_ERR_SERVER_CHECK, 1);
        if (failureCount < allowServerCheckIndex) {
            Log.i("Ringdroid", "failureCount " + failureCount +
                  " is less than " + allowServerCheckIndex);
            // Just show a simple "write error" message
            showFinalAlert(exception, errorString);
            return;
        }

        final SpannableString message = new SpannableString(
                errorString + ". " +
                getResources().getText(R.string.error_server_prompt));
        Linkify.addLinks(message, Linkify.ALL);

        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(R.string.alert_title_failure)
            .setMessage(message)
            .setPositiveButton(
                R.string.server_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        prefsEditor.putInt(PREF_ERR_SERVER_ALLOWED,
                                           SERVER_ALLOWED_YES);
                        prefsEditor.commit();
                        
                    }
                })
            .setNeutralButton(
                R.string.server_later,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        prefsEditor.putInt(PREF_ERR_SERVER_CHECK,
                                           1 + allowServerCheckIndex * 2);
                        Log.i("Ringdroid",
                              "Won't check again until " +
                              (1 + allowServerCheckIndex * 2) +
                              " errors.");
                        prefsEditor.commit();
                        
                    }
                })
            .setNegativeButton(
                R.string.server_never,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        prefsEditor.putInt(PREF_ERR_SERVER_ALLOWED,
                                           SERVER_ALLOWED_NO);
                        prefsEditor.commit();
                        
                    }
                })
            .setCancelable(false)
            .show();

        // Make links clicky
        ((TextView)dialog.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
    
    private void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos)
            mEndPos = mMaxPos;

        mCaption = 
            mSoundFile.getFiletype() + ", " +
            mSoundFile.getSampleRate() + " Hz, " +
            mSoundFile.getAvgBitrateKbps() + " kbps, " +
            formatTime(mMaxPos) + " " +
            getResources().getString(R.string.time_seconds);
        mInfo.setText(mCaption);

        updateDisplay();
    }
    
    private void resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0);
        mEndPos = mWaveformView.secondsToPixels(15.0);
    }
    private String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }
    
    private String formatDecimal(double x) {
        int xWhole = (int)x;
        int xFrac = (int)(100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    private synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    //音乐播放
    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            //开始、结束位置
            int startFrame = mWaveformView.secondsToFrames(
                mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(
                mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (mCanSeekAccurately && startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(
                        mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(),
                                          startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    System.out.println("Exception trying to play file subset");
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }
            //播放完成
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                    public synchronized void onCompletion(MediaPlayer arg0) {
                        handlePause();
                    }
                });
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception e) {
            showFinalAlert(e, getResources().getText(R.string.play_error));
            return;
        }
    }
    
    private void showFinalAlert(Exception e, CharSequence message) {
    	//CharSequence message = getResources().getText(messageResourceId);
        CharSequence title;
        if (e != null) {
//            Log.e("Ringdroid", "Error: " + message);
//            Log.e("Ringdroid", getStackTrace(e));
            title = getResources().getText(R.string.alert_title_failure);
            
        } else {
            Log.i("Ringdroid", "Success: " + message);
            title = getResources().getText(R.string.alert_title_success);
        }

        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                R.string.alert_ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        
                    }
                })
            .setCancelable(false)
            .show();
    }
    
    private void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
        } else {
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
            mPlayButton.setContentDescription(getResources().getText(R.string.play));
        }
    }
        
    private void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }
    
    private synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                float saveVel = mFlingVelocity;

                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(
            getResources().getText(R.string.start_marker) + " " +
            formatTime(mStartPos));
        mEndMarker.setContentDescription(
            getResources().getText(R.string.end_marker) + " " +
            formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
	    if (!mStartVisible) {
		// Delay this to avoid flicker
		mHandler.postDelayed(new Runnable() {
			public void run() {
			    mStartVisible = true;
			    mStartMarker.setAlpha(255);
			}
		    }, 0);
	    }
	} else {
	    if (mStartVisible) {
		mStartMarker.setAlpha(0);
		mStartVisible = false;
	    }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() +
            mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
	    if (!mEndVisible) {
		// Delay this to avoid flicker
		mHandler.postDelayed(new Runnable() {
			public void run() {
			    mEndVisible = true;
			    mEndMarker.setAlpha(255);
			}
		    }, 0);
	    }
	} else {
	    if (mEndVisible) {
		mEndMarker.setAlpha(0);
		mEndVisible = false;
	    }
            endX = 0;
        }

        mStartMarker.setLayoutParams(
            new AbsoluteLayout.LayoutParams(
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                startX,
                mMarkerTopOffset));

        mEndMarker.setLayoutParams(
            new AbsoluteLayout.LayoutParams(
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                endX,
                mWaveformView.getMeasuredHeight() -
                mEndMarker.getHeight() - mMarkerBottomOffset));
    }

    private Runnable mTimerRunnable = new Runnable() {
            public void run() {
                // Updating an EditText is slow on Android.  Make sure
                // we only do the update if the text has actually changed.
                if (mStartPos != mLastDisplayedStartPos &&
                    !mStartText.hasFocus()) {
                    mStartText.setText(formatTime(mStartPos));
                    mLastDisplayedStartPos = mStartPos;
                }

                if (mEndPos != mLastDisplayedEndPos &&
                    !mEndText.hasFocus()) {
                    mEndText.setText(formatTime(mEndPos));
                    mLastDisplayedEndPos = mEndPos;
                }

                mHandler.postDelayed(mTimerRunnable, 100);
            }
        };
    
    
	

	@Override
	public void waveformTouchStart(float x) {
		// TODO Auto-generated method stub
		mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
	}

	@Override
	public void waveformTouchMove(float x) {
		// TODO Auto-generated method stub
		 mOffset = trap((int)(mTouchInitialOffset + (mTouchStart - x)));
	        updateDisplay();
	}

	@Override
	public void waveformTouchEnd() {
		// TODO Auto-generated method stub
		mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = System.currentTimeMillis() -
            mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs(
                    (int)(mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec &&
                    seekMsec < mPlayEndMsec) {
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int)(mTouchStart + mOffset));
            }
        }
	}

	@Override
	public void waveformFling(float x) {
		// TODO Auto-generated method stub
		mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int)(-x);
        updateDisplay();
	}

	@Override
	public void waveformDraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markerFocus(com.example.ViewClass.MarkerView marker) {
		// TODO Auto-generated method stub
		mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(new Runnable() {
                public void run() {
                    updateDisplay();
                }
            }, 100);
	}
	
	@Override
	public void markerKeyUp() {
		// TODO Auto-generated method stub
		mKeyDown = false;
        updateDisplay();
	}

	@Override
	public void markerDraw() {
		// TODO Auto-generated method stub
		
	}
	
	private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    private int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

	@Override
	public void markerTouchStart(com.example.ViewClass.MarkerView marker, float pos) {
		// TODO Auto-generated method stub
		mTouchDragging = true;
        mTouchStart = pos;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
	}

	@Override
	public void markerTouchMove(com.example.ViewClass.MarkerView marker, float pos) {
		// TODO Auto-generated method stub
		float delta = pos - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int)(mTouchInitialStartPos + delta));
            mEndPos = trap((int)(mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int)(mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
	}

	@Override
	public void markerTouchEnd(com.example.ViewClass.MarkerView marker) {
		// TODO Auto-generated method stub
		mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
	}

	

	@Override
	public void markerLeft(com.example.ViewClass.MarkerView marker, int velocity) {
		// TODO Auto-generated method stub
		mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
	}

	@Override
	public void markerRight(com.example.ViewClass.MarkerView marker, int velocity) {
		// TODO Auto-generated method stub
		mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
	}

	@Override
	public void markerEnter(com.example.ViewClass.MarkerView marker) {
		// TODO Auto-generated method stub
		
	}
	

	

	
    

}
