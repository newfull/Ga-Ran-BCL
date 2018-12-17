package vn.bcl.garanbcl.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.adapter.CategoryPagerAdapter;
import vn.bcl.garanbcl.model.Category;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.Order;
import vn.bcl.garanbcl.model.Solution;
import vn.bcl.garanbcl.model.SubCategory;


public class MenuFragment extends Fragment{

    private ArrayList<Category> categoryList;
    private ArrayList<SubCategory> subCategoryList;
    private ArrayList<Item> itemList;
    private ArrayList<Solution> solutionList;
    private ArrayList<Order> orderList;

    public MenuFragment() {}

    View view;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        //TODO: get Data from DB to populate View!
        prepareData();

        // Get the ViewPager and set it's CategoryPagerAdapter so that it can display items
        ViewPager vpItem = (ViewPager) view.findViewById(R.id.vpItem);
        CategoryPagerAdapter categoryPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager(), getActivity(), solutionList);
        vpItem.setOffscreenPageLimit(categoryPagerAdapter.getCount());
        vpItem.setAdapter(categoryPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabCategory = view.findViewById(R.id.tabCategory);
        tabCategory.setupWithViewPager(vpItem);

        for (int i = 0; i < tabCategory.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabCategory.getTabAt(i);
            tab.setCustomView(categoryPagerAdapter.getTabView(i));
        }

        return view;
    }

    private void prepareData()
    {
        categoryList = new ArrayList<Category>();
        subCategoryList = new ArrayList<SubCategory>();
        itemList = new ArrayList<Item>();
        solutionList = new ArrayList<Solution>();
        orderList = new ArrayList<Order>();

        categoryList.add(new Category(1, "All", R.drawable.all));

        categoryList.add(new Category(2, "Food", R.drawable.food));
        subCategoryList.add(new SubCategory(1, 2, "Hamburger"));
        subCategoryList.add(new SubCategory(2, 2, "Pizza"));
        subCategoryList.add(new SubCategory(3, 2, "Kebab"));
        subCategoryList.add(new SubCategory(4, 2, "Dessert"));

        categoryList.add(new Category(3, "Drinks", R.drawable.drink));
        subCategoryList.add(new SubCategory(5, 3, "Water"));
        subCategoryList.add(new SubCategory(6, 3, "Soda"));
        subCategoryList.add(new SubCategory(7, 3, "Tea"));
        subCategoryList.add(new SubCategory(8, 3, "Coffee"));
        subCategoryList.add(new SubCategory(9, 3, "Fruit Juice"));

        categoryList.add(new Category(4, "Clothing & Accessories", R.drawable.clothing));
        subCategoryList.add(new SubCategory(10, 4, "T-Shirt"));
        subCategoryList.add(new SubCategory(11, 4, "Jean"));
        subCategoryList.add(new SubCategory(12, 4, "Jacket"));
        subCategoryList.add(new SubCategory(13, 4, "Polo"));
        subCategoryList.add(new SubCategory(14, 4, "Short"));
        subCategoryList.add(new SubCategory(15, 4, "Sweatshirt"));
        subCategoryList.add(new SubCategory(16, 4, "Shoes"));
        subCategoryList.add(new SubCategory(17, 4, "Accessories"));

        categoryList.add(new Category(5, "Technology", R.drawable.technology));
        subCategoryList.add(new SubCategory(18, 5, "Computer"));
        subCategoryList.add(new SubCategory(19, 5, "Tablet"));
        subCategoryList.add(new SubCategory(20, 5, "Cell Phone"));
        subCategoryList.add(new SubCategory(21, 5, "TV & Video"));
        subCategoryList.add(new SubCategory(22, 5, "Camera & Photo"));
        subCategoryList.add(new SubCategory(23, 5, "Video Game"));
        subCategoryList.add(new SubCategory(24, 5, "Wearable"));

        //Hamburger
        itemList.add(new Item(1, 2, 1, "Ultimate Hamburger", 5.0, "https://assets.epicurious.com/photos/57c5c6d9cf9e9ad43de2d96e/master/pass/the-ultimate-hamburger.jpg"));
        itemList.add(new Item(2, 2, 1, "Double Cheese Burger", 7.0, "https://1.bp.blogspot.com/-i2e3XPfVwYw/V9GgRgn2Y3I/AAAAAAAAxeM/Ih2LoXrSQr0NBgFKLeupxYNzwGZXBv1VwCLcB/s1600/Hardees-Classic-Double-Cheeseburger.jpg"));

        //Pizza
        itemList.add(new Item(3, 2, 2, "Pepperoni", 10.99, "https://www.cicis.com/media/1138/pizza_trad_pepperoni.png"));
        itemList.add(new Item(4, 2, 2, "Mixed Pizza", 11.20, "http://icube.milliyet.com.tr/YeniAnaResim/2016/10/24/tavada-pizza-tarifi-7857137.Jpeg"));
        itemList.add(new Item(5, 2, 2, "Neapolitan Pizza", 12.10, "https://img.grouponcdn.com/deal/2SVzinBnAH17zHrq3HNpurto2gpK/2S-700x420/v1/c700x420.jpg"));

        //Kebab
        itemList.add(new Item(6, 2, 3, "Garlic-Tomato Kebab", 16.60, "http://www.seriouseats.com/recipes/assets_c/2016/08/20160703-Grilled-Lemon-Garlic-Chicken-Tomato-Kebabs-Basil-Chimichurri-emily-matt-clifton-7-thumb-1500xauto-433447.jpg"));
        itemList.add(new Item(7, 2, 3, "Adana Kebab", 15.0, "http://www.muslumkebap.com/Contents/Upload/MuslumAdana_qyz0b34a.p3w.jpg"));
        itemList.add(new Item(8, 2, 3, "Eggplant Kebab", 18.0, "http://www.muslumkebap.com/Contents/Upload/MuslumPatlicanKebap_y41olk55.kpc.jpg"));
        itemList.add(new Item(9, 2, 3, "Shish Kebab", 10.0, "http://2.bp.blogspot.com/-4_8T6g3qOCU/VVz3oASO5lI/AAAAAAAAUHo/rEw1XlZoxqQ/s1600/Shish.jpg"));

        //Dessert
        itemList.add(new Item(10, 2, 4, "Pistachio Baklava", 10.0, "http://www.karakoygulluoglu.com/fistikli-kare-baklava-32-15-B.jpg"));
        itemList.add(new Item(11, 2, 4, "Palace Pistachio Baklava", 12.50, "http://www.karakoygulluoglu.com/fistikli-havuc-dilim-baklava-1kg-31-14-B.jpg"));
        itemList.add(new Item(12, 2, 4, "Mixed Baklava in Tray", 60.0, "http://www.karakoygulluoglu.com/karisik-baklava-1-tepsi-66-31-B.jpg"));
        itemList.add(new Item(13, 2, 4, "Tel Kadayıf with Pistachio", 9.0, "http://www.karakoygulluoglu.com/fistikli-tel-kadayif-1-39-44-B.jpg"));

        //Water
        itemList.add(new Item(14, 3, 5, "0.5 liter", 0.30, "http://cdn.avansas.com/assets/59479/erikli-su-0-5-lt-12-li-1-zoom.jpg"));

        //Soda
        itemList.add(new Item(15, 3, 6, "Coca Cola 0.3l", 0.70, "https://images-na.ssl-images-amazon.com/images/I/818i%2BQm07UL._SL1500_.jpg"));
        itemList.add(new Item(16, 3, 6, "Coca Cola 0.2l", 1.00, "https://images-na.ssl-images-amazon.com/images/I/511rL-aYd9L._SL1000_.jpg"));
        itemList.add(new Item(17, 3, 6, "Fanta 0.3l", 0.70, "http://www.germandeli.com/product-images/Fanta-Orange-Orange-Soda-33l_main-1.jpg"));
        itemList.add(new Item(18, 3, 6, "Fanta 0.5l", 1.10, "http://www.germandeli.com/product-images/Fanta-Orange-0-5-liter-bottle_main-1.jpg"));
        itemList.add(new Item(19, 3, 6, "Sprite 0.5l", 1.10, "https://cdn.grofers.com/app/images/products/full_screen/pro_312.jpg"));

        //Tea
        itemList.add(new Item(20, 3, 7, "Turkish Tea", 20.0, "https://images.hepsiburada.net/assets/Taris/500/Taris_3092120.jpg"));

        //Coffee
        itemList.add(new Item(21, 3, 8, "Turkish Coffee", 20.0, "https://cdn3.volusion.com/p3y5v.vg2ps/v/vspfiles/photos/TCW-XME02-2.jpg?1500568557"));

        //Fruit Juice
        itemList.add(new Item(22, 3, 9, "Mixed Fruit Juice", 1.0, "https://images.hepsiburada.net/assets/Taris/500/Taris_4791763.jpg"));
        itemList.add(new Item(23, 3, 9, "Apricot Fruit Juice", 1.0, "http://cdn.avansas.com/assets/53313/cappy-meyve-suyu-kayisi-1-lt-0-zoom.jpg"));
        itemList.add(new Item(24, 3, 9, "Orange Fruit Juice", 1.0, "https://images.hepsiburada.net/assets/Taris/500/Taris_4791980.jpg"));

        //Tshirt
        itemList.add(new Item(25, 4, 10, "Black T-Shirt", 20.0, "https://www.kaft.com/static/images/tee/0628_1.jpg"));
        itemList.add(new Item(26, 4, 10, "Blue T-Shirt", 20.0, "https://www.kaft.com/static/images/tee/0428_1.jpg"));
        itemList.add(new Item(27, 4, 10, "White T-Shirt", 20.0, "https://www.kaft.com/static/images/tee/0603_1.jpg"));

        //Jean
        itemList.add(new Item(28, 4, 11, "Blue Jean", 30.0, "https://sky-static.mavi.com/sys-master/maviTrImages/h61/h8c/8959419908126/0037823516_image_3.jpg_Default-MainProductImage"));
        itemList.add(new Item(29, 4, 11, "Dark Colored Jean", 30.0, "https://www.mensfitness.com/sites/mensfitness.com/files/styles/gallery_slideshow_image/public/1280-nudie-lean-jean.jpg?itok=rFDKRlFd"));

        //Jacket
        itemList.add(new Item(30, 4, 12, "Winter Jacket", 90.0, "https://images.thenorthface.com/is/image/TheNorthFace/NF0A2TCN_7D6_hero?$PDP-SCHEMA$"));
        itemList.add(new Item(31, 4, 12, "Leather Jacket", 110.0, "https://www.revzilla.com/product_images/0091/7906/scorpion1909_leather_jacket_black.jpg"));

        //polo
        itemList.add(new Item(32, 4, 13, "White Polo Shirt", 30.99, "https://www.hemington.com.tr/beyaz-triko-polo-giza-pamuk-tshirt-5513-86-B.jpg"));
        itemList.add(new Item(33, 4, 13, "Fernando Muslera", 999.99, "http://cdn.gsstore.org/UPLOAD/PRODUCT/thumb/K023-E75365_401_1_large.JPG"));

        //Short
        itemList.add(new Item(34, 4, 14, "Galatasaray Short", 999.99, "http://static.barcin.com/web/images/products/659037-628/nike-galatasaray-2015-2016-sezonu-dis-saha-erkek-sort-original-big.jpg"));
        itemList.add(new Item(35, 4, 14, "LeBron Basketball Short", 40.0, "http://www.swishbasketball.co.uk/media/catalog/product/cache/1/image/1500x/b661d95dd61f6b4c6be8ac50936dce4f/n/k/nk-718924-657_1.jpg"));

        //Sweatshirt
        itemList.add(new Item(36, 4, 15, "NY Sweatshirt", 50.99, "https://www.aksesuarix.com/images/products/03/18/21/31821_buyuk.jpg"));
        itemList.add(new Item(37, 4, 15, "Disnet Women Sweatshirt", 45.50, "https://uniqlo.scene7.com/is/image/UNIQLO/goods_09_182987"));

        //Shoes
        itemList.add(new Item(38, 4, 16, "Vans Shoes", 67.30, "https://m.media-amazon.com/images/G/01/2017/home/october2017/3424427-p-MULTIVIEW._CB514432018_.jpg"));
        itemList.add(new Item(39, 4, 16, "Converse All Star", 55.44, "https://m.media-amazon.com/images/G/01/zappos/landing/pages/mensclothing/MelodyTest1/MensShoes1._V506596164_.jpg"));

        //Accessories
        itemList.add(new Item(40, 4, 17, "Silver Bracelet", 220.0, "https://www.brighton.com/photos/product/giant/369560S17291/-/size-os.jpg"));
        itemList.add(new Item(41, 4, 17, "Gold Bracelet", 330.0, "http://scene7.zumiez.com/is/image/zumiez/pdp_hero/The-Gold-Gods-Gold-Cuban-Link-Bracelet-_244592-front.jpg"));
        itemList.add(new Item(42, 4, 17, "Leather Bracelet", 30.0, "https://images-na.ssl-images-amazon.com/images/I/71TtlBcKFWL._UL1500_.jpg"));

        //Computer
        itemList.add(new Item(43, 5, 18, "Apple iMac i5", 1710.0, "https://www.apple.com/v/imac/e/images/overview/og_image.png?201709270947"));
        itemList.add(new Item(44, 5, 18, "Apple MacBook i7", 1200.0, "https://brain-images-ssl.cdn.dixons.com/3/2/10165823/l_10165823_001.jpg"));

        //tablet
        itemList.add(new Item(45, 5, 19, "Galaxy 8inch Tablet", 660.0, "http://images.samsung.com/is/image/samsung/p5/tr/tablets/galaxy-tab-s2-001.png?$ORIGIN_PNG$"));
        itemList.add(new Item(46, 5, 19, "Asus ZenPad 10inch", 340.0, "https://images-na.ssl-images-amazon.com/images/I/91aibqJgIDL._SL1500_.jpg"));
        itemList.add(new Item(47, 5, 19, "iPad Mini 4", 780.0, "https://www.bhphotovideo.com/images/images2500x2500/apple_mk9n2ll_a_128gb_ipad_mini_4_1185478.jpg"));
        itemList.add(new Item(48, 5, 19, "iPad Pro", 1110.0, "https://www.bhphotovideo.com/images/images2500x2500/apple_mlmv2ll_a_9_7_ipad_pro_128gb_1241265.jpg"));

        //Cell Phone
        itemList.add(new Item(49, 5, 20, "Samsung S8", 555.0, "https://s7d2.scene7.com/is/image/SamsungUS/GS8_Front_20170712?$product-details-jpg$"));
        itemList.add(new Item(50, 5, 20, "iPhone 7", 660.0, "https://centrecomstatic.s3.amazonaws.com/images/upload/0038722_0.png"));
        itemList.add(new Item(51, 5, 20, "iPhone X", 1259.00, "https://cdn.macrumors.com/article-new/2017/09/iphonexdesign.jpg"));
        itemList.add(new Item(52, 5, 20, "LG G6", 440.0, "http://www.lg.com/au/images/smartphones/MD05812152/gallery/G6-medium06.jpg"));
        itemList.add(new Item(53, 5, 20, "Xiaomi Mi Mix 2", 350.0, "https://image4.geekbuying.com/ggo_pic/2017-09-11/Xiaomi-Mi-Mix-2-5-99-Inch-6GB-64GB-Smartphone-Black-465274-.jpg"));
        itemList.add(new Item(54, 5, 20, "Galaxy Note 8", 880.0, "https://images-na.ssl-images-amazon.com/images/I/41GZvrGM5bL.jpg"));

        //TV
        itemList.add(new Item(55, 5, 21, "Samsung 40inch TV", 330.0, "https://multimedia.bbycastatic.ca/multimedia/products/1500x1500/103/10317/10317673.jpg"));

        //Camera
        itemList.add(new Item(56, 5, 22, "Canon EOS 650D", 925.0, "https://shop.usa.canon.com/wcsstore/ExtendedSitesCatalogAssetStore/eos-t7i-18-55_1_xl.jpg"));
        itemList.add(new Item(57, 5, 22, "Go Pro 5", 1300.0, "https://www.bhphotovideo.com/images/images2500x2500/gopro_chdhx_501_hero5_black_1274419.jpg"));
        itemList.add(new Item(58, 5, 22, "Nikon 5600D", 800.0, "https://cdn-4.nikon-cdn.com/e/Q5NM96RZZo-YRYNeYvAi9beHK4x3L-8go_p7JUL6JpQM8B_IxDfxyg==/Views/1575_D5600_left.png"));

        //Video Game
        itemList.add(new Item(59, 5, 23, "Playstation 4 Pro", 420.99, "https://rukminim1.flixcart.com/image/1408/1408/gamingconsole/g/a/t/playstation-4-ps4-500-sony-dualshock-4-controller-original-imadrhehpvvetkgf.jpeg?q=90"));
        itemList.add(new Item(60, 5, 23, "FİFA 18", 40.0, "https://compass-ssl.xbox.com/assets/79/b1/79b130b1-d2a5-4858-98c8-5eecc16902bf.jpg?n=FF18_GLP-Page-Hero-1084_1920x600_02.jpg"));
        itemList.add(new Item(61, 5, 23, "NBA 2K 18", 40.0, "https://static-ca.ebgames.ca/images/products/731461/3max.jpg"));
        itemList.add(new Item(62, 5, 23, "Footbal Manager 18", 40.0, "http://www-footballmanager-com-18-public.s3.amazonaws.com/footballmanager/news/fm_2018_release_date_7.png"));
        itemList.add(new Item(64, 5, 23, "Playstation 4 Controller", 30.0, "http://media.4rgos.it/i/Argos/6257637_R_Z001A?$Web$&$DefaultPDP570$&$WebPDPBadge570$&topright=empty&bottomleft=empty"));
        itemList.add(new Item(65, 5, 23, "Call of Duty WW2", 40.0, "https://img.bolumsonucanavari.com/images/Upload/2dbedeb3-21a7-4294-8940-7ab68832e3f2.jpg"));

        //Wearable
        itemList.add(new Item(66, 5, 24, "Galaxy Gear S2", 220.0, "http://media.4rgos.it/i/Argos/5207042_R_Z002A?$Web$&$DefaultPDP570$"));
        itemList.add(new Item(67, 5, 24, "Galaxy Gear S3", 330.0, "http://www.samsung.com/global/galaxy/gear-s3/images/gear-s3-highlights_design_ft.jpg"));
        itemList.add(new Item(68, 5, 24, "Apple Watch", 440.0, "https://brain-images-ssl.cdn.dixons.com/2/9/10151992/l_10151992_001.jpg"));
        itemList.add(new Item(69, 5, 24, "Xiaomi Mi Band 2", 20.0, "https://image4.geekbuying.com/ggo_pic/2016-06-03/201606309322239menyh.jpg"));

         /** Populates solutionList.
         * For each category, gets sub-categories, items and a map
         * showing the connection between the sub-category and its items.*/


        for (Category categoryItem : categoryList)
        {
            // Temporary list of the current sıb-categories
            ArrayList<SubCategory> tempSubCategoryList = new ArrayList<>();

            // Temporary list of the current items
            ArrayList<Item> tempItemList = new ArrayList<>();

            // Temporary map
            Map<SubCategory, ArrayList<Item>> itemMap = new HashMap<SubCategory, ArrayList<Item>>();

            // categoryId == 1 means category with all items and sub-categories.
            // That's why i add all the sub-categories and items directly.
            if (categoryItem.id == 1)
            {
                itemMap = getItemMap(subCategoryList);

                solutionList.add(new Solution(categoryItem, subCategoryList, itemList, itemMap));
            }
            else
            {
                tempSubCategoryList = getSubCategoryListByCategoryId(categoryItem.id);
                tempItemList = getItemListByCategoryId(categoryItem.id);
                itemMap = getItemMap(tempSubCategoryList);

                solutionList.add(new Solution(categoryItem, tempSubCategoryList, tempItemList, itemMap));
            }
        }
    }

    private Map<SubCategory, ArrayList<Item>> getItemMap(ArrayList<SubCategory> subCategoryList)
    {
        Map<SubCategory, ArrayList<Item>> itemMap = new HashMap<SubCategory, ArrayList<Item>>();

        for (SubCategory subCategory : subCategoryList)
        {
            itemMap.put(subCategory, getItemListBySubCategoryId(subCategory.id));
        }

        return itemMap;
    }

    private ArrayList<Item> getItemListBySubCategoryId(int subCategoryId)
    {
        ArrayList<Item> tempItemList = new ArrayList<Item>();

        for (Item item : itemList)
        {
            if (item.subCategoryId == subCategoryId)
            {
                tempItemList.add(item);
            }
        }

        return tempItemList;
    }

    private ArrayList<SubCategory> getSubCategoryListByCategoryId(int categoryId)
    {
        ArrayList<SubCategory> tempSubCategoryList = new ArrayList<SubCategory>();

        for (SubCategory subCategory : subCategoryList)
        {
            if (subCategory.categoryId == categoryId)
            {
                tempSubCategoryList.add(new SubCategory(subCategory));
            }
        }

        return tempSubCategoryList;
    }

    private ArrayList<Item> getItemListByCategoryId(int categoryId)
    {
        ArrayList<Item> tempItemList = new ArrayList<Item>();

        for (Item item : itemList)
        {
            if (item.categoryId == categoryId)
            {
                tempItemList.add(item);
            }
        }

        return tempItemList;
    }
}
