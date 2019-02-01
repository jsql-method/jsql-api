{include file="header.tpl"}

{if isset($smarty.get.section) }
    {foreach from=$Dashboard item=section name=section}
        {if isset($section.data) && isset($smarty.get.page)}
            {foreach from=$section.data item=page name=page}
                {if $page.page == $smarty.get.page}
                    {assign var="tpl" value=$page.template scope=parent} 
                    {break}     
                {/if}
            {/foreach}
        {else}
            {if isset($section.template)}
                {assign var="tpl" value=$section.template scope=parent}    
            {/if}
        {/if}
    {/foreach}
{/if}

{if isset($tpl)}
{include "pages/$tpl"}
{/if}

{include file="footer.tpl"}