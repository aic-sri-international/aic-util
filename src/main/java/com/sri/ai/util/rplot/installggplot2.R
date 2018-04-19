chooseCRANmirror(ind=81)
install.packages('ggplot2')
print("------------ all done ------------")

install.packages('rattle.data')
library(rattle.data)




library(ggplot2)

l0 <- list(c(1,2,3,4,5))
l1 <- list(c(1,2,3,4,5))
l2 <- list(c(1,2,3,4,5))
l3 <- list(c(1,2,3,4,5))


df5 <- data.frame(l0,l1,l2,l3)
colnames(df5) <- c("l0","l1","l2","l3")
p <- ggplot(df5,aes(x=l0,y=l1)) + geom_point() +  geom_smooth(method="loess")
p
dev.new()
m <- plot(p)


library(ggplot2)
theme_set(theme_bw())  # pre-set the bw theme.
data("midwest", package = "ggplot2")
# midwest <- read.csv("http://goo.gl/G1K41K")  # bkup data source

# Scatterplot
gg <- ggplot(midwest, aes(x=area, y=poptotal)) + 
    geom_point(aes(col=state, size=popdensity)) + 
    geom_smooth(method="loess", se=F) + 
    xlim(c(0, 0.1)) + 
    ylim(c(0, 500000)) + 
    labs(subtitle="Area Vs Population", 
         y="Population", 
         x="Area", 
         title="Scatterplot", 
         caption = "Source: midwest")

plot(gg)
midwest
df5
