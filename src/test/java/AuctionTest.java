import com.so.junit.rules.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuctionTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public ItemRule itemRule = new ItemRule();

    /*private List<Item> buildItems() {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item("code1", 100));
        items.add(new Item("code2", 1500));
        return items;
    }*/

    private List<Item> buildItems() {
        return itemRule.getItems();
    }

    @Test
    public void testSellItemAtAuction() {
        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldPen = auction.getItems().get(0);
        auction.sendBidForItem(goldPen, 100);
        auction.sendBidForItem(goldPen, 200);
        auction.sendBidForItem(goldPen, 300);
        assertEquals("There are 3 bids for the gold pen", auction.getAllBidsForItem(goldPen).size(), 3);
        Bid winningBid = auction.sellItem(goldPen);
        assertEquals("The winning bid is", winningBid.getAmount(), 300);
    }

    @Test
    public void testCannotSellItemAtAuction1() {
        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldWatch = auction.getItems().get(1);
        try {
            auction.sendBidForItem(goldWatch, 1000);
            auction.sendBidForItem(goldWatch, 800);
            assertEquals("There are 2 bids for the gold watch", auction.getAllBidsForItem(goldWatch).size(), 2);
            Bid winningBid = auction.sellItem(goldWatch);
        } catch (UnsupportedOperationException e) {
            assertEquals(e.getMessage(), "Cannot sell item " + goldWatch.getCode() + " below the reserve price");
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCannotSellItemAtAuction2() {
        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldWatch = auction.getItems().get(1);
        auction.sendBidForItem(goldWatch, 1000);
        auction.sendBidForItem(goldWatch, 800);
        assertEquals("There are 2 bids for the gold watch", auction.getAllBidsForItem(goldWatch).size(), 2);
        Bid winningBid = auction.sellItem(goldWatch);
    }

    @Test
    public void testCannotSellItemAtAuction3() {
        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldWatch = auction.getItems().get(1);
        auction.sendBidForItem(goldWatch, 1000);
        auction.sendBidForItem(goldWatch, 800);
        assertEquals("There are 2 bids for the gold watch", auction.getAllBidsForItem(goldWatch).size(), 2);

        expectedException.equals(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot sell item " + goldWatch.getCode() + " below the reserve price");
        Bid winningBid = auction.sellItem(goldWatch);
    }
}
