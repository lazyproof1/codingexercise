package com.sutodo.steven.todosurprise;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private final String SAVEFILE = "surprise.txt";
    private ArrayList<String> aList;
    private ArrayAdapter<String> aListAdapter;
    private ListView aListView;
    private ArrayList<Integer> aSelectedListIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init ListView
        aListView = (ListView) findViewById(R.id.lsTodo);
        aListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        aList = new ArrayList<String>();
        aSelectedListIndex = new ArrayList<Integer>();

        // Read stored tasks
        mReadFile();

        // Adapter and Listener
        aListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, aList);
        aListView.setAdapter(aListAdapter);
        mListViewListener();

        // Testing purpose
        mPopulateList();
    }

    /**
     * Populate list for testing purposes
     **/
    private void mPopulateList()
    {
        aListAdapter.add("Add task");
        aListAdapter.add("Select checkboxes");
        aListAdapter.add("Long press to copy");
        aListAdapter.add("Remove selected tasks in Action bar");
        aListAdapter.add("The quick brown fox jumps over the lazy dog");
        aListAdapter.add("Duck");
        aListAdapter.add("Chicken");
        aListAdapter.add("Sheep");
        aListAdapter.add("Horse");
    }

    /**
     * Add an item in List upon button click
     * @param pView     View of the current screen
     **/
    public void mOnAddItem(View pView)
    {
        // Find id
        EditText lNewTodo = (EditText) findViewById(R.id.etNewItem);

        // Add
        String lTodoText = lNewTodo.getText().toString();
        aListAdapter.add(lTodoText);

        // Cleanup
        lNewTodo.setText("");

        // Save
        mSaveList();
        mScrollBottom();
    }

    /**
     * Display dialog for additional info
     **/
    protected void mShowAbout()
    {
        // Inflate
        View lMsgView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder lAlertDlg = new AlertDialog.Builder(this);
        lAlertDlg.setIcon(R.drawable.ic_launcher);

        // Display title
        lAlertDlg.setTitle(R.string.app_name);
        lAlertDlg.setView(lMsgView);
        lAlertDlg.create();
        lAlertDlg.show();
    }

    /**
     * Setup ListView Listener
     * Clicking on an item:
     * -    Will edit task
     * Upon adding a new task:
     * -    EditText will become empty
     * -    List gets updated
     * -    Magic
     **/
    private void mListViewListener()
    {
        aListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id)
            {
                // Add index to list
                if (!item.isSelected())
                {
                    aSelectedListIndex.add(pos);
                }
                else
                {
                    aSelectedListIndex.remove(pos);
                }
            }
        });
        aListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id)
            {
                // Get Task's text
                String lTask = aList.get(pos);

                // Insert into Clipboard
                android.content.ClipboardManager lClipManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData lClip = ClipData.newPlainText("", lTask);
                lClipManager.setPrimaryClip(lClip);

                // Notify copy
                mToastAlert("Task copied to clipboard:\n" + lTask);
                return true;
            }
        });
    }

    /**
     * Remove selected tasks
     **/
    public void mRemove()
    {
        if(!aSelectedListIndex.isEmpty())
        {
            for(int i : aSelectedListIndex)
            {
                aList.remove(i);
            }
            aSelectedListIndex.clear();

            // Remove checkbox ticks
            aListView.clearChoices();
            aListView.requestLayout();

            // Save changes
            aListAdapter.notifyDataSetChanged();
            mSaveList();
            mToastAlert("Selected task(s) removed");
        }
        else
        {
            mToastAlert("No tasks removed");
        }
    }

    /**
     *
     **/
    public void mShowSurprise()
    {
        mToastAlert("S-Staph, I have nothing D:");
    }

    /**
     * Display quick Toast alert
     * @param pMessage  Message to display
     */
    private void mToastAlert(String pMessage)
    {
        Toast.makeText(this, pMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Spoiler alert: it scrolls to the bottom
     **/
    private void mScrollBottom()
    {
        aListView.post(new Runnable()
        {
            @Override
            public void run()
            {
                // Select the last row so it will scroll into view...
                aListView.setSelection(aListView.getCount() - 1);
            }
        });
    }

    /**
     * Save list items for persistence
     **/
    private void mSaveList()
    {
        File lDir = getFilesDir();
        File lFile = new File(lDir, SAVEFILE);
        try
        {
            FileUtils.writeLines(lFile, aList);
        }
        catch(IOException lE)
        {
            lE.printStackTrace();
        }
    }

    /**
     * Read saved list
     **/
    private void mReadFile()
    {
        File lDir = getFilesDir();
        File lFile = new File(lDir, SAVEFILE);
        try
        {
            aList = new ArrayList<String>(FileUtils.readLines(lFile));
        }
        catch(IOException lE)
        {
            lE.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.act_remove)
        {
            mRemove();
            return true;
        }
        else if(id == R.id.act_surprise)
        {
            mShowSurprise();
            return true;
        }
        else if(id == R.id.act_about)
        {
            mShowAbout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
