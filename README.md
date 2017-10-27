# TableView

最近正好学到自定义TableView，也简单了解了一下其他可能的实现方式。

1、首行，首列和内容区域分别用三个recyclerView来实现TableView。

2、ListView在item中套滑动控件并通过联动来实现效果。

这里采用完全自定义ViewGroup的方式实现一个TableView。

## 如何使用

#### DEMO

![TableView](https://raw.githubusercontent.com/lhc20040808/Pictures/master/res/图片/table_view_gif.gif)



目前提供两个抽象的适配器`BaseCommonTableAdapter`和`BaseTableAdapter`。用户可以继承这两个适配器，也可以自己实现`ITableAdapter`接口。

#### 使用步骤

1、最新版本都会发布到jcenter中

```groovy
compile 'com.lhc:TableView:1.0.0'
```

2、实现一个适配器，使用方式很接近`ListView#adapter`

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

3、设置适配器

```java
  tableView = (TableView) findViewById(R.id.tableView);
  tableView.setAdapter(adapter);
```

4、更新数据

```
adapter.notifyDateSetChange();
```

#### TODO

//TODO 支持首行或者首列不显示

//TODO 可以绘制divider