import com.so.junit.rules.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class AuctionTest {

    @Rule
    public ItemRule itemRule = new ItemRule();

    private List<Item> buildItems() {
        return itemRule.getItems();
    }

    @Test
    public void testSellItemAtAuction() {
        int bid1 = 100;
        int bid2 = 200;
        int bid3 = 300;
        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldPen = auction.getItems().get(0);
        auction.sendBidForItem(goldPen, bid1);
        auction.sendBidForItem(goldPen, bid2);
        auction.sendBidForItem(goldPen, bid3);
        Assertions.assertThat(auction.getAllBidsForItem(goldPen)).as("There are 3 bids for the gold pen").hasSize(3);
        Assertions.assertThat(auction.getAllBidsForItem(goldPen)).as("The bids are " + bid1 + "," + bid2 + "," + bid3).extracting("amount").contains(bid1, bid2, bid3);
        Bid winningBid = auction.sellItem(goldPen);
        Assertions.assertThat(winningBid.getAmount()).as("The winning bid is " + bid3).isEqualTo(bid3);
    }

    @Test
    public void testCannotSellItemAtAuction3() {
        int bid1 = 1000;
        int bid2 = 800;
        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldWatch = auction.getItems().get(1);
        auction.sendBidForItem(goldWatch, bid1);
        auction.sendBidForItem(goldWatch, bid2);
        Assertions.assertThat(auction.getAllBidsForItem(goldWatch)).as("There are 2 bids for the gold watch").hasSize(2);
        Assertions.assertThat(auction.getAllBidsForItem(goldWatch)).as("The bids are " + bid1 + "," + bid2).extracting("amount").contains(bid1, bid2);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> auction.sellItem(goldWatch))
                .withMessage("Cannot sell item " + goldWatch.getCode() + " below the reserve price");
    }

    @Test
    public void testExpectToFail() {
        int bid1 = 100;
        int bid2 = 200;
        int bid3 = 300;

        Auction auction = new Auction();
        auction.addItems(buildItems());
        Item goldPen = auction.getItems().get(0);
        auction.sendBidForItem(goldPen, bid1);
        auction.sendBidForItem(goldPen, bid2);
        auction.sendBidForItem(goldPen, bid3);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(auction.getAllBidsForItem(goldPen)).as("There are 3 bids for the gold pen").hasSize(2);
        softly.assertThat(auction.getAllBidsForItem(goldPen)).as("The bids are " + bid1 + "," + bid2 + "," + bid3).extracting("amount").contains(bid1 + 1, bid2 + 1, bid3 + 1);
        Bid winningBid = auction.sellItem(goldPen);
        softly.assertThat(winningBid.getAmount()).as("The winning bid is " + bid3).isEqualTo(bid3 + 1);
        softly.assertAll();
    }

}
