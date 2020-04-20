#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/netfilter.h>
#include <linux/netfilter_ipv4.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/udp.h>
#include <stdbool.h>

#define PORT_1	8080
#define PORT_2	50070


static struct nf_hook_ops *nfho = NULL;
struct connection
{
	u32 saddr;	//From address
	u32 daddr;	//To address
	int numPack;
} connection;

size_t count = 0; 
struct connection l [10000];

int countPack(u32 saddr, u32 daddr){
	int pos;
	pos = 0;
	if(count == 0){
		l[0].saddr = saddr;
		l[0].daddr = daddr;
		l[0].numPack = 1;
		count++;
		return pos;
	}
	else{
		bool isNew = 1;
		int i = 0;
		for(i; i<count; i++){
			if(l[i].saddr == saddr && l[i].daddr == daddr){
				l[i].numPack++;
				isNew = 0;
				pos = i;
				return pos;
			}
		}
		
		if(count < 10000 && isNew){
			l[count].saddr = saddr;
			l[count].daddr = daddr;
			pos = count;			
			count++;
			return pos;
			
		}
	}
	return pos;
}

static unsigned int recordPackets(const struct nf_hook_ops *ops,
                                   struct sk_buff *skb,
                                   const struct net_device *in,
                                   const struct net_device *out,
                                   int (*okfn)(struct sk_buff *))
{
	struct iphdr *iph;          
    	struct tcphdr *tcph;        
    	u16 sport;		//From Port
	u16 dport;           //To Port
    	u32 saddr; 		//From address
	u32 daddr;          //To address

	

	
    	if (!skb)
        {
		return NF_ACCEPT;
	}
	
	//IP Header.
	iph = ip_hdr(skb);          
	
        
	//TCP header
	tcph = tcp_hdr(skb); 
	
	
	saddr = ntohl(iph->saddr);
    	daddr = ntohl(iph->daddr);
	sport = ntohs(tcph->source);
    	dport = ntohs(tcph->dest);

	
    	if (!(sport == PORT_1 || sport == PORT_2))
	{
        	return NF_ACCEPT;
	}

	int pos;
	pos = countPack(saddr, daddr);


	printk("%pI4h:%d -> %pI4h:%d, NumPacks: %d\n", &saddr, sport, &daddr, dport, l[pos].numPack);

	return NF_ACCEPT;
}



static int hook_setup(void)
{
	nfho = (struct nf_hook_ops*)kcalloc(1, sizeof(struct nf_hook_ops), GFP_KERNEL);


	
	nfho->hook = (nf_hookfn*) recordPackets;	
	nfho->hooknum = NF_INET_PRE_ROUTING;	
	nfho->pf = PF_INET;			
	nfho->priority = NF_IP_PRI_FIRST;	

	nf_register_net_hook(&init_net, nfho);
return 0;
}

static void hook_exit(void)
{
	nf_unregister_net_hook(&init_net, nfho);
	kfree(nfho);
}


module_init(hook_setup);
module_exit(hook_exit);

MODULE_AUTHOR("Amanda, Lalliet and Catherine");
MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("Recording network packages in a kernel message statement");
