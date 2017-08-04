# TableView

最近正好学到自定义TableView，也简单了解了一下其他可能的实现方式。

可以首行，首列和内容区域分别用三个recyclerView来实现TableView。

也有公司用ListView，在item中套自定义控件来实现效果。

不讨论以上思路的好坏，这里采用完全自定义ViewGroup的方式实现一个TableView。

## 如何使用

先来看看实现后的效果，录频之后有点失真。

//TODO 支持首行或者首列不显示

//TODO 可以绘制divider

![TableView](https://raw.githubusercontent.com/lhc20040808/Pictures/master/res/图片/table_view_gif.gif)



目前提供两个抽象的适配器`BaseCommonTableAdapter`和`BaseTableAdapter`。用户可以继承这两个适配器，也可以自己实现`ITableAdapter`接口。

使用步骤

1、实现一个适配器，使用方式很接近`ListView#adapter`

```java
public class TableAdapter extends BaseCommonTableAdapter {
    private Context context;
    List<String> listofFirstRow;
    List<Model> listofFirstColumn;


    public TableAdapter(Context context, List<String> listOfFirstRow, List<Model> listOfFirstColumn) {
        this.context = context;
        this.listofFirstColumn = listOfFirstColumn;
        this.listofFirstRow = listOfFirstRow;
    }

    @Override
    public int getRow() {
        return listofFirstColumn.size();
    }

    @Override
    public int getColumn() {
        return listofFirstRow.size();
    }

    @Override
    public View getTitleView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_title, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText("Title");
        return convertView;
    }

    @Override
    public View getFirstRowView(int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_first_row, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        if (column % 2 == 0) {
            textView.setText("type2");
        } else {
            textView.setText("type1");
        }
        return convertView;
    }

    @Override
    public View getFirstColumnView(int row, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_first_column, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        TextView textCode = (TextView) convertView.findViewById(R.id.tv_code);
        textView.setText("Name");
        textCode.setText("code" + row);
        return convertView;
    }

    @Override
    public View getBodyView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_body, parent, false);
        }
        LinearLayout ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText("行:" + row + " 列:" + column);

        textView.setTextColor(Color.parseColor("#ffffff"));
        ll_container.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.table_bg));
        return convertView;
    }


}
```

2、设置适配器

```java
  tableView = (TableView) findViewById(R.id.tableView);
  tableView.setAdapter(adapter);
```

3、更新数据

```
adapter.notifyDateSetChange();
```



## 如何实现

#### 实现一个回收池

回收池的实现也很简单，采用栈来存储回收的View，不同类型的View存储在不同的栈中。所以我们要做以下几步。

根据类型数量构造相同数量的栈，每个栈对应一个类型的View

根据类型将View添加进回收池

根据类型从相应的栈中取出之前回收的View

```java
    /**
     * 设置View的类型数量
     * @param viewTypeCount
     */
    private void setViewTypeCount(int viewTypeCount) {
        if (viewTypeCount < 1) {
            throw new IllegalArgumentException("viewTypeCount 不能小于1");
        }

        Stack<View>[] scrapViews = new Stack[viewTypeCount];

        for (int i = 0; i < viewTypeCount; i++) {
            scrapViews[i] = new Stack<>();
        }

        this.mScrapViews = scrapViews;
        this.viewTypeCount = viewTypeCount;
    }

    /**
     * 获取回收池中的View
     * @param row 行数
     * @param column 列数
     * @return
     */
    View getScrapView(int row, int column) {
        int type = mAdapter.getItemViewType(row, column);
        checkTypeValue(type);

        return retrieveFromScrap(mScrapViews[type]);
    }

    /**
     * 获取栈顶的View
     * @param mScrapView
     * @return
     */
    private View retrieveFromScrap(Stack<View> mScrapView) {
        if (!mScrapView.isEmpty()) {
            return mScrapView.pop();
        } else {
            return null;
        }
    }

    /**
     * 将View加入回收池
     * @param view
     * @param row 行数
     * @param column 列数
     */
    void addScrapView(View view, int row, int column) {
        int type = mAdapter.getItemViewType(row, column);
        checkTypeValue(type);
        mScrapViews[type].add(view);
    }
```



#### 实现一个ViewGroup

onMeasure`

首先测量控件宽高，实现onMeasure方法，对ViewGroup的宽高设置无非就是match_parent(MeasureSpec.EXACTLY)，wrap_content(MeasureSpec.AT_MOST)。加载的时候如果行列数过多会该方法执行时间过长，产生卡顿，因为要在onMeasure中取每个子View的宽高保存到数组中。

//TODO to be continued